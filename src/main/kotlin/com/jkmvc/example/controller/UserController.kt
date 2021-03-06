package com.jkmvc.example.controller

import com.jkmvc.example.model.UserModel
import com.jkmvc.http.Controller
import com.jkmvc.orm.isLoaded
import java.io.File

/**
 * 用户管理
 * user manage
 */
class UserController: Controller()
{
    /**
     * 列表页
     * list page
     */
    public fun actionIndex()
    {
        // 查询所有用户 | find all users
        val users = UserModel.queryBuilder().findAll<UserModel>()
        // 渲染视图 | render view
        res.render(view("user/index", mutableMapOf("users" to users)))
    }

    /**
     * 详情页
     * detail page
     */
    public fun actionDetail()
    {
        // 获得路由参数id: 2种写法 | 2 ways to get route parameter: "id"
        // val id = req.getIntRouteParameter("id"); // req.getRouteParameter["xxx"]
        val id:Int? = req["id"] // req["xxx"]
        // 查询单个用户 | find a user
        //val user = UserModel.queryBuilder().where("id", id).find<UserModel>()
        val user = UserModel(id)
        if(!user.isLoaded()){
            res.render("用户[$id]不存在")
            return
        }
        // 渲染视图 | render view
        val view = view("user/detail")
        view["user"] = user; // 设置视图参数 | set view data
        res.render(view)
    }

    /**
     * 新建页
     * new page
     */
    public fun actionNew()
    {
        // 处理请求 | handle request
        if(req.isPost()){ //  post请求：保存表单数据 | post request: save form data
            // 创建空的用户 | create user model
            val user = UserModel()
            // 获得请求参数：3种写法 | 3 ways to get request parameter
            /* // 1 req.getParameter("xxx");
            user.name = req.getParameter("name");
            user.age = req.getIntParameter("age", 0)!!; // 带默认值 | default value
            */
            // 2 req["xxx"]
            user.name = req["name"];
            user.age = req["age"];

            // 3 Orm.values(req)
            user.values(req)
            user.create(); // create user
            // 重定向到列表页 | redirect to list page
            redirect("user/index");
        }else{ // get请求： 渲染视图 | get request: render view
            val view = view() // 默认视图为action名： user/new | default view's name = action：　user/new
            res.render(view)
        }
    }

    /**
     * 编辑页
     * edit page
     */
    public fun actionEdit()
    {
        // 查询单个用户 | find a user
        val user = UserModel(req["id"])
        if(!user.isLoaded()){
            res.render("用户[" + req["id"] + "]不存在")
            return
        }
        // 处理请求 | handle request
        if(req.isPost()){ //  post请求：保存表单数据 | post request: save form data
            // 获得请求参数：3种写法 | 3 way to get request parameter
            /* // 1 req.getParameter("xxx");
            user.name = req.getParameter("name");
            user.age = req.getIntParameter("age", 0)!!; // 带默认值 | default value
            */
            /*// 2 req["xxx"]
            user.name = req["name"];
            user.age = req["age"];
            */
            // 3 Orm.values(req)
            user.values(req)
            user.update() // update user
            // 重定向到列表页 | redirect to list page
            redirect("user/index");
        }else{ // get请求： 渲染视图 | get request: render view
            val view = view() // 默认视图为action名： user/edit | default view's name = action：　user/edit
            view["user"] = user; // 设置视图参数 |  set view data
            res.render(view)
        }
    }

    /**
     * 删除
     * delete action
     */
    public fun actionDelete()
    {
        val id:Int? = req["id"]
        // 查询单个用户 | find a user
        val user = UserModel(id)
        if(!user.isLoaded()){
            res.render("用户[$id]不存在")
            return
        }
        // 删除 | delete user
        user.delete();
        // 重定向到列表页 | redirect to list page
        redirect("user/index");
    }

    public fun actionUplad()
    {
        // 获得路由参数id
        // val id = req.getIntRouteParameter("id"); // req.getRouteParameter["xxx"]
        val id:Int? = req["id"] // req["xxx"]
        // 查询单个用户
        val user = UserModel(id)
        if(!user.isLoaded()){
            res.render("用户[$id]不存在")
            return
        }

        // 检查并处理上传文件
        val uploaded = req.checkUpload {
            val path = UserModel.prepareUploadDir() + it.fieldName
            it.write(File(path))
            path;
        }
        if(uploaded){ //  post请求：保存表单数据
            user.values(req)
            user.update()
            // 重定向到列表页
            redirect("user/index");
        }else{ // get请求： 渲染视图
            val view = view() // 默认视图为action名： user/upload
            view["user"] = user; // 设置视图参数
            res.render(view)
        }
    }
}