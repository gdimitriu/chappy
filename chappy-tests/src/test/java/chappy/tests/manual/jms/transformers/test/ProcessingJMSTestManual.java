/**
 * 
 */
package chappy.tests.manual.jms.transformers.test;

import chappy.clients.jms.ChappyJMSLogin;
import chappy.clients.jms.ChappyJMSLogout;
import chappy.interfaces.jms.IJMSTransactionHolder;
import chappy.interfaces.jms.protocol.IJMSStatus;

/**
 * @author Gabriel Dimitriu
 *
 */
public class ProcessingJMSTestManual {

	/**
	 * 
	 */
	public ProcessingJMSTestManual() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ProcessingJMSTestManual().firstLoginLogout();

	}

	public void firstLoginLogout() {
		ChappyJMSLogin login = new ChappyJMSLogin("system", "system", true);
		try {
			login.createConnectionToServer("localhost", 61616);
			login.send();
			while(login.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
			System.out.println(login.getStatus());
			System.out.println(login.getTransactionException());
			System.out.println(login.getTransactionErrorMessage());
			if (!login.hasException()) {
				IJMSTransactionHolder transaction = login.createTransactionHolder();
				System.out.println(transaction.getCookie().getTransactionId());
				System.out.println(transaction.getCookie().getUserName());
				ChappyJMSLogout logout = new ChappyJMSLogout(transaction);
				logout.send();
				while(logout.getStatus().equals(IJMSStatus.REPLY_NOT_READY)) Thread.sleep(1000);
				System.out.println(logout.getStatus());
				System.out.println(logout.getTransactionException());
				System.out.println(logout.getTransactionErrorMessage());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
