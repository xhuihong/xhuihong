package com.xhh.servlet;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xhh.utils.CodeConfig;

import redis.clients.jedis.Jedis;

/**
 * SendCodeServlet
 */
@WebServlet("/SendCodeServlet")
public class SendCodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public SendCodeServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 获取手机号
		String phoneNo = request.getParameter("phone_no");
		if (phoneNo == null) {
			return;
		}
		// 创建jedis对象
		Jedis jedis = new Jedis(CodeConfig.HOST, CodeConfig.PORT);
		// 拼接计数器的key
		String countKey = CodeConfig.PHONE_PREFIX + phoneNo + CodeConfig.COUNT_SUFFIX;
		// 根据计数器的key从redis中获取count
		String redisCount = jedis.get(countKey);
		//判断计数器的值是否为空
		if(redisCount==null) {
			//证明是第一次发送
			jedis.setex(countKey, CodeConfig.SECONDS_PER_DAY, "1");
			//判断次数是否为三
		}else if("3".equals(redisCount)) {
			response.getWriter().write("limit");
			jedis.close();
			return;
		}else {
			//计数器的值加一
			jedis.incr(countKey);
		}
		// 拼接保存在redis中的key
		String codeKey = CodeConfig.PHONE_PREFIX + phoneNo + CodeConfig.PHONE_SUFFIX;
		System.out.println(codeKey);
		// 生成6位验证码
		String code = getCode(CodeConfig.CODE_LEN);
		// 向手机号发送验证码
		System.out.println(code);

		jedis.setex(codeKey, CodeConfig.CODE_YIMEOUT, code);
		// 给浏览器响应一个字符串true
		response.getWriter().write("true");
		jedis.close();

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	// 随机生成验证码的方法
	private String getCode(int len) {
		String code = "";
		for (int i = 0; i < len; i++) {
			int rand = new Random().nextInt(10);
			code += rand;
		}
		return code;
	}

}
