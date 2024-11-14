package com.metoo.nrsm.core.config.socket;

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.nrsm.core.api.service.*;
import com.metoo.nrsm.core.config.redis.MyRedisManager;
import com.metoo.nrsm.core.config.socket.linux.command.exec.CommandFactory;
import com.metoo.nrsm.core.config.socket.linux.command.ping.pingUtils.ExecUtil;
import com.metoo.nrsm.core.config.socket.linux.command.ping.pingUtils.PingFactory;
import com.metoo.nrsm.core.config.socket.linux.command.ping.pingUtils.PingUtil;
import com.metoo.nrsm.core.config.socket.thread.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 * 只读属性 readyState 表示连接状态，可以是以下值：
 *
 * 0 - 表示连接尚未建立。
 *
 * 1 - 表示连接已建立，可以进行通信。
 *
 * 2 - 表示连接正在进行关闭。
 *
 * 3 - 表示连接已经关闭或者连接不能打开。
 *
 * @OnOpen 表示有浏览器链接过来的时候被调用
 * @OnClose 表示浏览器发出关闭请求的时候被调用
 * @OnMessage 表示浏览器发消息的时候被调用
 * @OnError 表示报错了
 */

/**
 * Bug：
 *  1，关闭连接时，定时任务未结束会导致远程Api创建Redis key
 *     1-1：增加关闭标识，nspm创建Key时判断连接是否已关闭
 *     1-2：自定义互斥锁，ws开启连接，创建自定义锁，ws关闭连接，将自定义互斥锁删除；Api使用时判断互斥锁是否存在（好像这两种方式都一样。。。）
 */
@ServerEndpoint("/notice/{userId}")
@Component
@Slf4j
public class NoticeEndpoint {

    //记录连接的客户端
    public static Map<String, Session> clients = new ConcurrentHashMap<>();

    // 记录连接的客户端的参数，定时发送消息
    public static Map<String, Map<String, String>> clientsParams = new ConcurrentHashMap<>();

    // 定时任务 避免同一账号多地登录下，定时任务回显数据无法区分用户
    public static Map<String, Map<String, String>> taskParams = new ConcurrentHashMap<>();//

    /**
     * userId关联sid（解决同一用户id，在多个web端连接的问题）
     */
    public static Map<String, Set<String>> conns = new ConcurrentHashMap<>();

    private String sid = null;

    private String userId;

    @Autowired
    private static MyRedisManager redisWss = new MyRedisManager("ws");

    /**
     * 注入SpringBean
     */

    private static ITerminalService terminalService;
    @Autowired
    public void setNetworkElementService(ITerminalService terminalService) {
        NoticeEndpoint.terminalService = terminalService;
    }



    private static INetworkElementService networkElementService;
    @Autowired
    public void setNetworkElementService(INetworkElementService networkElementService) {
        NoticeEndpoint.networkElementService = networkElementService;
    }


    private static IWUserService wUserService;
    @Autowired
    public void setNetworkElementService(IWUserService wUserService) {
        NoticeEndpoint.wUserService = wUserService;
    }

    private static IFlowStatisticsService flowStatisticsService;
    @Autowired
    public void setFlowStatisticsService(IFlowStatisticsService flowStatisticsService) {
        NoticeEndpoint.flowStatisticsService = flowStatisticsService;
    }

    private static PingUtil pingUtil;
    @Autowired
    public void setPingUtil(PingUtil pingUtil) {
        NoticeEndpoint.pingUtil = pingUtil;
    }


    /**
     * 连接成功后调用的方法
     * @param session
     * @param userId
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        // 方法一：前端发送用户信息（明文）
        // 方法二：做分布式登录 SSO。部署到同一个服务器，认证授权使用同一个项目
        NoticeWebsocketResp resp = NoticeEndpoint.wUserService.selectObjById(Long.parseLong(userId));
        if(true){
            this.sid = UUID.randomUUID().toString();
            this.userId = userId;
            clients.put(this.sid, session);

            Map userMap = taskParams.get(this.sid);
            if(userMap == null){// 单独记录sid,避免同一账号多地登录
                userMap = new HashMap();
                userMap.put("userId", this.userId);
                taskParams.put(this.sid, userMap);
            }

            Set<String> clientSet = conns.get(userId);
            if (clientSet==null){
                clientSet = new HashSet<>();
                conns.put(userId, clientSet);
            }

            clientSet.add(this.sid);
            log.info(this.sid + "用户Id:" + userId + "连接开启！");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {

        log.info(this.sid + " Connection break！");

        // 使用异步执行，无序打断线程，ws断开连接时，线程会自动停止？
//        PingThread.interruptExec(sid);

        // 清除 redis
        try {
            outCycle:for (String key : taskParams.keySet()){

                log.info(this.sid + " start del！");

                Map<String, String> params = taskParams.get(key);

                log.info(this.sid + " task:" + params);

                for(String type : params.keySet()){

                    log.info(this.sid + " task: for");

                    if(!type.equals("") && !type.equals("userId")){

                        String hkey = this.sid + ":" + type + ":0";

                        log.info(this.sid + " hkey " + hkey);

                        Object value = redisWss.get(hkey);

                        log.info(this.sid + " hkey value " + value);

                        if(value != null){
                            redisWss.remove(hkey);
                        }

                        String hkey1 = this.sid + ":" + type + ":1";

                        log.info(this.sid + " hkey1 " + hkey1);

                        value = redisWss.get(hkey1);

                        log.info(this.sid + " hkey1 value " + value);

                        if(value != null){
                            redisWss.remove(hkey1);
                        }
                    }else {
                        continue ;// outCycle
                    }
                }
            }
        } catch (Exception e){
            log.info(this.sid + " Fail to delete！");
        } finally {

            /**
             * 清除 记录连接的客户端
             */
            clients.remove(this.sid);

            /**
             * 清除定时任务信息
             *
             */
            taskParams.remove(this.sid);

            log.info(this.sid + " Close end！");
        }

    }

    /**
     * 判断是否连接的方法
     * @return
     */
    public static boolean isServerClose() {
        if (NoticeEndpoint.clients.values().size() == 0) {
            log.info("已断开");
            return true;
        }else {
            log.info("已连接");
            return false;
        }
    }

    /**
     * 发送给所有用户
     * @param noticeType
     */
    public static void sendMessage(String noticeType){
        NoticeWebsocketResp noticeWebsocketResp = new NoticeWebsocketResp();
        noticeWebsocketResp.setNoticeType(noticeType);
        sendMessage(noticeWebsocketResp);
    }

    /**
     * 发送给所有用户
     * @param noticeWebsocketResp
     */
    public static void sendMessage(NoticeWebsocketResp noticeWebsocketResp){
        String message = JSONObject.toJSONString(noticeWebsocketResp);
        for (Session session1 : NoticeEndpoint.clients.values()) {
            try {
                session1.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据用户id发送给某一个用户
     * **/
    public static void sendMessageByUserId(String userId, NoticeWebsocketResp noticeWebsocketResp) {
        if (!StringUtils.isEmpty(userId)) {
            String message = JSONObject.toJSONString(noticeWebsocketResp);
            Set<String> clientSet = conns.get(userId);
            if (clientSet != null) {
                Iterator<String> iterator = clientSet.iterator();
                while (iterator.hasNext()) {
                    String sid = iterator.next();
                    Session session = clients.get(sid);
                    if (session != null) {
                        try {
                            session.getBasicRemote().sendText(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message
     */
    @OnMessage(maxMessageSize = 1048576)
    public void onMessage(String message) {
        if("heartCheck".equals(message)){
            return;
        }

        // 接收消息
        try {
            parseParams2(message);
            Map params = JSONObject.parseObject(message, Map.class);
            Object prm = parseParam2(sid, params);

            // 校验参数，调用指定api
            if ("101".equals(params.get("noticeType"))) {
//                taskSendMessageByUserId(sid, getTerminal(prm));
                ThreadPool.getInstance().addThread(new GetTerminalRunnable(sid, prm, taskParams, clients));
            }
            if ("102".equals(params.get("noticeType"))) {
//                taskSendMessageByUserId2(sid, getPartitionTerminal(prm));
                ThreadPool.getInstance().addThread(new GetPartitionTerminalRunnable(sid, prm, taskParams, clients));

            }
            if ("103".equals(params.get("noticeType"))) {
//                taskSendMessageByUserId(sid, getTerminalCount(prm));
                ThreadPool.getInstance().addThread(new GetTerminalCountRunnable(sid, prm, taskParams, clients));
            }
            if ("104".equals(params.get("noticeType"))) {
//                taskSendMessageByUserId(sid, getTerminalCount(prm));
                ThreadPool.getInstance().addThread(new GetUnitTerminalRunnable(sid, prm, taskParams, clients));
            }
            if ("201".equals(params.get("noticeType"))) {
//                taskSendMessageByUserId(sid, getNeAvailable(prm));
                ThreadPool.getInstance().addThread(new GetNeAvailableRunnable(sid, prm, taskParams, clients));
            }
            if ("202".equals(params.get("noticeType"))) {
//                taskSendMessageByUserId(sid, getNeSnmpStatus(prm));
                ThreadPool.getInstance().addThread(new GetNeSnmpStatusRunnable(sid, params, taskParams, clients));
            }
            if ("301".equals(params.get("noticeType"))) {
//                taskSendMessageByUserId(sid, getFlux(prm));
                ThreadPool.getInstance().addThread(new GetFluxRunnable(sid, prm, taskParams, clients));
            }
            if ("401".equals(params.get("noticeType"))) {
                ThreadPool.getInstance().addThread(new GetNeApOnlineRunnable(sid, prm, taskParams, clients));
            }
            if ("ping".equals(params.get("noticeType")) || "traceroute".equals(params.get("noticeType"))
                    || "traceroute6".equals(params.get("noticeType"))) {
                CommandFactory.Exec.exec(sid, getParam(params.get("noticeType").toString(), params.get("ip").toString()), 0);
            }
            if ("nslookup".equals(params.get("noticeType"))) {
//                Nslookup nslookup = new Nslookup();
//                nslookup.test(sid, getParam(params.get("noticeType").toString(),
//                        params.get("ip").toString(), "- 127.0.0.1"));
                CommandFactory.Exec.exec(sid, getParam(params.get("noticeType").toString(),
                    params.get("ip").toString(), "- 127.0.0.1"), 1);
        }
            if ("close".equals(params.get("noticeType"))) {
                CommandFactory.Exec.interruptExec(sid);
            }
            if ("ping2".equals(params.get("noticeType"))) {// 测试断开
                PingFactory.PingExec.exec(sid, params.get("ip").toString());
            }
            if ("pingClose2".equals(params.get("noticeType"))) {
                ExecUtil.interruptExec(sid);
            }
            if ("pingClose3".equals(params.get("noticeType"))) {
                PingFactory.PingExec.interruptExec(sid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getParam(String action, String ip){
        if(action.equals("ping")){
            String[] str2 = {action, ip.trim()};
            return str2;
        }
        String[] str = {action, ip.trim()};
        return str;
    }

    public String[] getParam(String prefix, String ip, String suffix){
        String[] str = {prefix, ip.trim(), suffix};
        return str;
    }

    // 处理数据 关联到一个账号（存在一个账号多地登陆时，无法分别发送给登录用户，只能统一发送给这个账号的所有登录同一数据）
    public void parseParams(String message){
        Map map = (Map) JSON.parse(message);
        if(map != null && !map.isEmpty()){
            Map userMap = clientsParams.get(this.userId);
            if(map.get("noticeType") != null){
                if(userMap.get(map.get("noticeType").toString()) != null){
                    userMap.remove(map.get("noticeType").toString());
                    userMap.put(map.get("noticeType"), message);
                }else{
                    userMap.put(map.get("noticeType"), message);
                }
                clientsParams.put(this.userId, userMap);
            }
        }
    }

    /**
     * 保存参数，使用定时任务发送指定数据（解决同一账号多地登陆时，分别响应）
     * @param message
     */
    public void parseParams2(String message){

        log.info("parsePatams2===================" + message);
        Map map = null;
        map = (Map) JSON.parse(message);
        if(map != null && !map.isEmpty()){
            if(map.get("time") == null || "".equals(map.get("time"))){
                Map userMap = taskParams.get(this.sid);
                if(map.get("noticeType") != null){
                    map.put("sessionId", this.sid);
                    if(userMap.get(map.get("noticeType").toString()) != null){
                        userMap.remove(map.get("noticeType").toString());
                        userMap.put(map.get("noticeType"), JSONObject.toJSONString(map));
                    }else{
                        userMap.put(map.get("noticeType"),  JSONObject.toJSONString(map));
                    }
                    taskParams.put(this.sid, userMap);
                }
            }else{
                taskParams.get(this.sid).clear();
            }
        }
    }

    public NoticeWebsocketResp getTerminal(Object params){
        NoticeWebsocketResp resp = NoticeEndpoint.terminalService.getTerminal(JSONObject.toJSONString(params));
        return resp;
    }

    public NoticeWebsocketResp getPartitionTerminal(Object params){
        NoticeWebsocketResp resp = NoticeEndpoint.terminalService.getPartitionTerminal(JSONObject.toJSONString(params));
        return resp;
    }

    public NoticeWebsocketResp getTerminalCount(Object params){
        NoticeWebsocketResp resp = NoticeEndpoint.terminalService.getTerminalCount(JSONObject.toJSONString(params));
        return resp;
    }

    public NoticeWebsocketResp getUnitTerminal(Object params){
        NoticeWebsocketResp resp = NoticeEndpoint.terminalService.getUnitTerminal(JSONObject.toJSONString(params));
        return resp;
    }


    public NoticeWebsocketResp getNeAvailable(Object params){
        NoticeWebsocketResp resp = NoticeEndpoint.networkElementService.getNeAvailable(JSONObject.toJSONString(params));
        return resp;
    }

    public NoticeWebsocketResp getNeSnmpStatus(Object params){
        NoticeWebsocketResp resp = NoticeEndpoint.networkElementService.getNeSnmpStatus(JSONObject.toJSONString(params));
        return resp;
    }

    public NoticeWebsocketResp getNeApOnline(Object params){
        NoticeWebsocketResp resp = NoticeEndpoint.networkElementService.getOnlineAp(JSONObject.toJSONString(params));
        return resp;
    }


    public NoticeWebsocketResp getFlux(Object params){
        NoticeWebsocketResp resp = NoticeEndpoint.flowStatisticsService.getFlux(JSONObject.toJSONString(params));
        return resp;
    }
    /**
     * 发生错误时的回调函数
     * @param error
     */
    @OnError
    public void onError(Throwable error) {
        log.info("========= Error ==========");
        error.printStackTrace();
    }

    /**
     * 根据用户sid发送消息
     * @param sid
     * @param noticeWebsocketResp
     */
    public static void taskSendMessageByUserId(String sid, NoticeWebsocketResp noticeWebsocketResp) {
        if (!StringUtils.isEmpty(sid)) {
            String message = JSONObject.toJSONString(noticeWebsocketResp);
            Map userMap = taskParams.get(sid);
            if (userMap != null) {
                Session session = clients.get(sid);
                if (session != null) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void taskSendMessageByUserId2(String sid, NoticeWebsocketResp noticeWebsocketResp) {
        if (!StringUtils.isEmpty(sid)) {
            String message = JSONObject.toJSONString(noticeWebsocketResp);
            Map userMap = taskParams.get(sid);
            if (userMap != null) {
                Session session = clients.get(sid);
                if (session != null) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public static boolean sendPingMessage(String sid, String message) {
        if (!StringUtils.isEmpty(sid)) {
            Map userMap = taskParams.get(sid);
            if (userMap != null) {
                Session session = clients.get(sid);
                if (session != null) {
                    try {
                        session.getBasicRemote().sendText(message);
                        return true;
                    }catch (IllegalStateException ie){
                        log.info("发送消息-中断");
                    } catch (IOException e) {
                        e.printStackTrace();
                        log.info("发送消息-IO");
                    }
                }
            }
        }
        return false;
    }

    public static boolean sendPingMessage2(String sid, String message){
        if (!StringUtils.isEmpty(sid)) {
            Map userMap = taskParams.get(sid);
            if (userMap != null) {
                Session session = clients.get(sid);
                if (session != null) {
                    try {
                        session.getBasicRemote().sendText(message);
                        return true;
                    }catch (IllegalStateException ie){
                        return false;// 线程关闭
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }else{
                    return false;
                }
            }
        }
        return false;
    }

    // 第一次加载异步执行terminal写入redis， 并查询是否为1数据，没有，页面刷新只需等待异步执行结束，或等待定时任务返回数据
    public static void taskSendMessageByUserId(String sid, String type) {
        if (!StringUtils.isEmpty(sid)) {
            Map userMap = taskParams.get(sid);
            if (userMap != null) {
                Session session = clients.get(sid);
                if (session != null) {
                    try {
                        // 生成key
                        String key = sid + ":" + type + ":" + "1";
                        Object v = redisWss.get(key);
                        log.info("send " + v);
                        if(v != null && !v.equals("")){
                            session.getBasicRemote().sendText(JSONObject.toJSONString(v));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 根据用户sid发送消息
     * // 逻辑不严谨，redis更新使用异步，导致数据更新显示不同步；同步更新redis
     * @param sid
     * @param noticeWebsocketResp
     */
    public static void taskSendMessageByRedis(String sid, NoticeWebsocketResp noticeWebsocketResp) {
        if (!StringUtils.isEmpty(sid)) {
            String message = JSONObject.toJSONString(noticeWebsocketResp);
            Map userMap = taskParams.get(sid);
            if (userMap != null) {
                Session session = clients.get(sid);
                if (session != null) {
                    try {
                        // 生成key
                        String key = sid + ":" + noticeWebsocketResp.getNoticeType() + ":" + "1";
                        Object v = redisWss.get(key);
                        log.info("send " + v);
                        if(v != null && !v.equals("")){
                            session.getBasicRemote().sendText(message);
                        }
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void taskSendMessageByUserIdSyncRedis(String sid, NoticeWebsocketResp noticeWebsocketResp) {
        if (!StringUtils.isEmpty(sid)) {
            String message = JSONObject.toJSONString(noticeWebsocketResp);
            Map userMap = taskParams.get(sid);
            if (userMap != null) {
                Session session = clients.get(sid);
                if (session != null) {
                    try {
                        // 生成key
                        String key = sid + ":" + noticeWebsocketResp.getNoticeType() + ":" + "1";
                        Object v = redisWss.get(key);
                        log.info("send " + v);
                        if(v != null && !v.equals("")){
                            session.getBasicRemote().sendText(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

//    @Scheduled(cron = "*/10 * * * * ?")
//    public void task1(){
//        outCycle:for (String key : taskParams.keySet()){ // 校验用户是否已断开，或断开时删除该用户定时任务信息
//            Map<String, String> params = taskParams.get(key);
//            for(String type : params.keySet()){
//                if(type.equals("101")){
//                    Map param = JSONObject.parseObject(params.get(type), Map.class);
//                    taskSendMessageByUserId(key, getTerminal(JSON.toJSONString(param)));
//                }else {
//                    continue ;// outCycle
//                }
//            }
//        }
//    }

    @Scheduled(cron = "*/30 * * * * ?")
    public void getTerminal(){
        for (String key : taskParams.keySet()){// 校验用户是否已断开，或断开时删除该用户定时任务信息
            Map<String, String> params = taskParams.get(key);
            if(StringUtil.isNotEmpty(params.get("101"))){
                Map param = JSONObject.parseObject(params.get("101"), Map.class);
                taskSendMessageByRedis(key, getTerminal(param));
                break;
            }
        }
    }

    @Scheduled(cron = "*/30 * * * * ?")
    public void getPartitionTerminal(){
        for (String key : taskParams.keySet()){// 校验用户是否已断开，或断开时删除该用户定时任务信息
            Map<String, String> params = taskParams.get(key);
            if(StringUtil.isNotEmpty(params.get("102"))){
                Map param = JSONObject.parseObject(params.get("102"), Map.class);
                taskSendMessageByRedis(key, getPartitionTerminal(param));
                break;
            }
        }
    }

    @org.springframework.scheduling.annotation.Scheduled(cron = "0 */1 * * * ?")
    public void getTerminalCount(){
        for (String key : taskParams.keySet()){// 校验用户是否已断开，或断开时删除该用户定时任务信息
            Map<String, String> params = taskParams.get(key);
            if(StringUtil.isNotEmpty(params.get("103"))){
                Map param = JSONObject.parseObject(params.get("103"), Map.class);
                taskSendMessageByRedis(key, getTerminalCount(param));
                break;
            }
        }
    }

    @org.springframework.scheduling.annotation.Scheduled(cron = "0 */1 * * * ?")
    public void getUnitTerminal(){
        for (String key : taskParams.keySet()){// 校验用户是否已断开，或断开时删除该用户定时任务信息
            Map<String, String> params = taskParams.get(key);
            if(StringUtil.isNotEmpty(params.get("104"))){
                Map param = JSONObject.parseObject(params.get("104"), Map.class);
                taskSendMessageByRedis(key, getUnitTerminal(param));
                break;
            }
        }
    }

    @Scheduled(cron = "*/30 * * * * ?")
    public void getNeAvailable(){
        outCycle:for (String key : taskParams.keySet()){// 校验用户是否已断开，或断开时删除该用户定时任务信息
            Map<String, String> params = taskParams.get(key);
            if(StringUtil.isNotEmpty(params.get("201"))){
                Map param = JSONObject.parseObject(params.get("201"), Map.class);
                taskSendMessageByRedis(key, getNeAvailable(param));
                break;
            }
        }
    }

    @Scheduled(cron = "*/30 * * * * ?")
    public void getNeSnmpStatus(){
        outCycle:for (String key : taskParams.keySet()){// 校验用户是否已断开，或断开时删除该用户定时任务信息
            Map<String, String> params = taskParams.get(key);
            if(StringUtil.isNotEmpty(params.get("202"))){
                Map param = JSONObject.parseObject(params.get("202"), Map.class);
                taskSendMessageByRedis(key, getNeSnmpStatus(param));
                break;
            }
        }
    }

    @org.springframework.scheduling.annotation.Scheduled(cron = "0 */1 * * * ?")
    public void getFlux(){
        for (String key : taskParams.keySet()){// 校验用户是否已断开，或断开时删除该用户定时任务信息
            Map<String, String> params = taskParams.get(key);
            if(StringUtil.isNotEmpty(params.get("301"))){
                Map param = JSONObject.parseObject(params.get("301"), Map.class);
                taskSendMessageByRedis(key, getFlux(param));
                break;
            }
        }
    }

    @org.springframework.scheduling.annotation.Scheduled(cron = "0 */1 * * * ?")
    public void getNeApOnline(){
        for (String key : taskParams.keySet()){// 校验用户是否已断开，或断开时删除该用户定时任务信息
            Map<String, String> params = taskParams.get(key);
            if(StringUtil.isNotEmpty(params.get("401"))){
                Map param = JSONObject.parseObject(params.get("401"), Map.class);
                taskSendMessageByRedis(key, getNeApOnline(param));
                break;
            }
        }
    }




//    public Object parseParam(String sessionId, Map param){
//        if(param != null){
//            Map map = new HashMap();
//            if(param.get("time") != null && !param.get("time").equals("")){
//                map.put("time", param.get("time"));
//            }
//            map.put("params", param.get("params"));
//            map.put("sessionId", sessionId);
//            return map;
//        }
//        return "";
//    }
//
    public Object parseParam2(String sessionId, Map param){
        if(param != null){
            param.put("sessionId", sessionId);
            return param;
        }
        return "";
    }

}
