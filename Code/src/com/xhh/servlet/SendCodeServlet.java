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
		// ��ȡ�ֻ���
		String phoneNo = request.getParameter("phone_no");
		if (phoneNo == null) {
			return;
		}
		// ����jedis����
		Jedis jedis = new Jedis(CodeConfig.HOST, CodeConfig.PORT);
		// ƴ�Ӽ�������key
		String countKey = CodeConfig.PHONE_PREFIX + phoneNo + CodeConfig.COUNT_SUFFIX;
		// ���ݼ�������key��redis�л�ȡcount
		String redisCount = jedis.get(countKey);
		//�жϼ�������ֵ�Ƿ�Ϊ��
		if(redisCount==null) {
			//֤���ǵ�һ�η���
			jedis.setex(countKey, CodeConfig.SECONDS_PER_DAY, "1");
			//�жϴ����Ƿ�Ϊ��
		}else if("3".equals(redisCount)) {
			response.getWriter().write("limit");
			jedis.close();
			return;
		}else {
			//��������ֵ��һ
			jedis.incr(countKey);
		}
		// ƴ�ӱ�����redis�е�key
		String codeKey = CodeConfig.PHONE_PREFIX + phoneNo + CodeConfig.PHONE_SUFFIX;
		System.out.println(codeKey);
		// ����6λ��֤��
		String code = getCode(CodeConfig.CODE_LEN);
		// ���ֻ��ŷ�����֤��
		System.out.println(code);

		jedis.setex(codeKey, CodeConfig.CODE_YIMEOUT, code);
		// ���������Ӧһ���ַ���true
		response.getWriter().write("true");
		jedis.close();

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	// ���������֤��ķ���
	private String getCode(int len) {
		String code = "";
		for (int i = 0; i < len; i++) {
			int rand = new Random().nextInt(10);
			code += rand;
		}
		return code;
	}

}
