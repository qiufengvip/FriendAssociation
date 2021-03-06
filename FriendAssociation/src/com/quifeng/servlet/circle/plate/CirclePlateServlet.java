package com.quifeng.servlet.circle.plate;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.quifeng.dao.circle.CircleDao;
import com.quifeng.dao.token.TokenDao;
/**
 * @desc   获取单个板块帖子
 * @author JZH
 *
 */
public class CirclePlateServlet {

	CircleDao circleDao = new CircleDao();
	TokenDao tokenDao = new TokenDao();
	
	public void queryOnlyTypePost(HttpServletRequest request, HttpServletResponse response) {
		//接值
		String placaid = request.getParameter("placaid");
		String token = request.getParameter("token");
		String size = request.getParameter("size");
		String page = request.getParameter("page");
		
		//创建返回数据的值
		Map<String, Object> datamap = new HashMap<String, Object>();
		//写入返回的数据
		PrintWriter out = null; 
		
		try {
			out = response.getWriter();
			if (size == null || size.equals("")) {
				size="10";
			}
			if (page == null || page.equals("") ) {
				page="1";
			}
			
			if (placaid==null||token==null||page==null||placaid.equals("")||token.equals("")||page.equals("")) {
				datamap.put("code", "-1");
				datamap.put("mgs", "参数不正确");
				out.print(JSON.toJSONString(datamap));
				out.close();
				return;
			}
			
			if (tokenDao.queryToken(token) == null) {
				datamap.put("code", "-1");
				datamap.put("msg", "token失效,请重新登陆");
				out.print(JSON.toJSONString(datamap));
				out.close();
				return;
			}
			
			List<Map<String, Object>> list = circleDao.getPostMM(placaid, size, page);
			if (list.size()==0) {
				datamap.put("code", "-1");
				datamap.put("msg", "已经到底了");
				out.print(JSON.toJSONString(datamap));
				out.close();
				return;
			}
			
			List<Map<String, Object>> data = new ArrayList<>();
			
			
			
			for (Map<String, Object> map : list) {
				//查看该帖子是否点赞或收藏
				Map<String, Object> zanAos = circleDao.queryUserZanAndAos(map.get("postid").toString(),token);
				Map<String, Object> postmap = new HashMap<String , Object>();
				Map<String, Object> userinfo = new HashMap<String , Object>();
				userinfo.put("uname", map.get("username"));
				userinfo.put("useravatar", map.get("useravatar"));
				postmap.put("userinfo", userinfo);
				//是否点赞或收藏
				if(!zanAos.get("zuid").toString().equals("0")){
					System.out.println(map.get("zuid"));
					postmap.put("isgreat", true);
				}
				else{
					postmap.put("isgreat", false);
				}
				if(!zanAos.get("auid").toString().equals("0")){
					postmap.put("collect", true);
				}
				else{
					postmap.put("collect", false);
				}
				postmap.put("type", map.get("1"));
				postmap.put("placaid", map.get("placaid"));
				postmap.put("postid", map.get("postid"));
				postmap.put("createtime", map.get("createtime"));
				postmap.put("posttext", map.get("posttext"));
				if (map.get("postimg")!=null ) {
					postmap.put("postimg", map.get("postimg"));
					postmap.put("type", map.get("2"));
				}
				if (map.get("postvideo")!=null ) {
					postmap.put("postvideo", map.get("postvideo"));
					postmap.put("type", map.get("3"));
				}
				System.out.println(postmap);
				data.add(postmap);
			}
			

			datamap.put("code", "200");
			datamap.put("msg", "获取成功");	
			datamap.put("data", data);
			
			
			out.print(JSON.toJSONString(datamap));
			System.out.println(datamap);
			out.close();
		} catch (Exception e) {
			datamap.put("code", "-1");
			datamap.put("msg", "请求异常");
			out.print(JSON.toJSONString(datamap));
			out.close();
			return;
		}
	}
	
}
