package com.metoo.nrsm.core.config.interceptor;

import com.metoo.nrsm.core.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MyInterceptor implements HandlerInterceptor {


    @Autowired
    private static IUserService userService;

    /**
     * handler 对应@RequestMapping对应的controller对象
     */
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        //这里我们是没办法拿到方法参数的，parameters是空的，但是可以拿到controller中注入的bean
//
////        HandlerMethod handlerMethod = (HandlerMethod)handler;
////        MethodParameter[] parameters = handlerMethod.getMethodParameters();
//        String id = request.getParameter("userId");
//        NoticeWebsocketResp resp = this.userService.selectObjById(1L);
//        if(resp.getNoticeStatus() == 1){
//            return true;//放行
//        }
//        return false;
//    }
}
