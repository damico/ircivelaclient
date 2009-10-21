package org.jdamico.ircblackboardserver.sys;

import java.io.IOException;

import org.jdamico.ircblackboardserver.web.JettyController;

public class Launch {
	public static void main(String[] args) throws IOException {
		JettyController jController = new JettyController();
		jController.init();
	}
}
