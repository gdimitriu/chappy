package chappy.interfaces.cookies;

public interface IChappyCookie {

	/**
	 * get the user name
	 * @return the user name
	 */
	String getUserName();

	/**
	 * set the user Name.
	 * @param nameUser
	 */
	void setUserName(String nameUser);

	/**
	 * get the transaction id.
	 * @return transaction id
	 */
	String getTransactionId();

	/**
	 * set the transaction id.
	 * @param transactionId transaction id
	 */
	void setTransactionId(String transactionId);

	/**
	 * generate the storage id.
	 * @return storage id for the hash.
	 */
	String generateStorageId();

}