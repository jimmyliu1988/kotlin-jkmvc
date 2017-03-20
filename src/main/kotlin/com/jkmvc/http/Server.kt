package com.jkmvc.http

import com.jkmvc.common.findConstructor
import com.jkmvc.common.findFunction
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.KFunction

/**
 * 服务端对象，用于处理请求
 *
 * @Package packagename
 * @category
 * @author shijianhang
 * @date 2016-10-6 上午9:27:56
 *
 */
object Server {
    /**
     * 处理请求
     *
     * @param HttpServletRequest req
     * @param HttpServletResponse res
     */
    public fun run(request: HttpServletRequest, response: HttpServletResponse) {
        // 构建请求与响应对象
        val req = Request(request);
        val res = Response(response);

        try {
            // 解析路由
            if (!req.parseRoute())
                throw RouteException("当前uri没有匹配路由：" + req.requestURI);

            // 调用路由对应的controller与action
            callController(req, res);
        }
        /* catch (e：RouteException)
        {
            // 输出404响应
            res.setStatus(404).send();
        }  */
        catch (e: Exception) {
            res.render("异常 - " + e.message)
        }

    }

    /**
     * 调用controller与action
     *
     * @param Request req
     * @param Response res
     */
    fun callController(req: Request, res: Response) {
        // 获得controller类
        val clazz:Class<*>? = ControllerLoader.getControllerClass(req.controller())
        if (clazz == null)
            throw RouteException ("Controller类不存在：" + req.controller());

        // 获得构造函数
        val cst: KFunction<*>? = clazz.kotlin.findConstructor(listOf(Request::class.java, Response::class.java))
        if(cst == null)
            throw RouteException ("Controller类无对应构造函数" + req.controller());

        // 创建controller
        val controller = cst.call(req, res);

        // 获得action方法
        val action: KFunction<*>? = clazz.kotlin.findFunction(req.action())
        if (action == null)
            throw RouteException ("类${clazz}不存在方法：$action");

        // 调用controller的action方法
        action.call(controller);
    }


}