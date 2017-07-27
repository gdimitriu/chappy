/**
    Copyright (c) 2017 Gabriel Dimitriu All rights reserved.
	DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.

    This file is part of chappy project.

    Chappy is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Chappy is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Chappy.  If not, see <http://www.gnu.org/licenses/>.
 */
package chappy.persistence.datanucleus;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import chappy.interfaces.cookies.CookieTransaction;
import chappy.interfaces.markers.ISystemFlowPersistence;
import chappy.interfaces.markers.ISystemLogsPersistence;
import chappy.interfaces.markers.ISystemUpgradePersistence;
import chappy.interfaces.persistence.ICustomStepPersistence;
import chappy.persistence.transaction.AbstractPersistenceTransaction;
/**
 * @author Gabriel Dimitriu
 *
 */

public class DatanucleusTransaction extends AbstractPersistenceTransaction {

	/** persistence manager who own this log Transaction */
	private PersistenceManager persistenceLogManager = null;
	
	/** persistence manager who own this upgrade Transaction */
	private PersistenceManager persistenceUpgradeManager = null;
	
	/** persistence manager who own this flow Transaction */
	private PersistenceManager persistenceFlowManager = null;
	
	/** persistence log Transaction */
	private Transaction logTransaction = null;
	
	/** persistence upgrade Transaction */
	private Transaction upgradeTransaction = null;
	
	/** persistence flow Transaction */
	private Transaction flowTransaction = null;
	
	/**
	 * 
	 */
	public DatanucleusTransaction() {
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#start()
	 */
	@Override
	public void start() {		
		if (getSystemLogPersistence().getFactory() == null) {
			return ;
		}
		if (logTransaction == null) {
			if (getSystemLogPersistence() != null) {
				persistenceLogManager  = ((PersistenceManagerFactory) getSystemLogPersistence().getFactory()).getPersistenceManager();
				if (persistenceLogManager != null) {
					logTransaction = persistenceLogManager.currentTransaction();
					logTransaction.begin();
				}
			}
		}
		if (upgradeTransaction == null) {
			if (getSystemUpgradePersistence() != null) {
				persistenceUpgradeManager  = ((PersistenceManagerFactory) getSystemUpgradePersistence().getFactory()).getPersistenceManager();
			}
		}
		if (flowTransaction == null) {
			if (getSystemFlowPersistence() != null) {
				persistenceFlowManager  = ((PersistenceManagerFactory) getSystemFlowPersistence().getFactory()).getPersistenceManager();
			}
		}
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#commit()
	 */
	@Override
	public void commit() {
		if (logTransaction != null) {
			 logTransaction.commit();
			 logTransaction = null;
		}
		if (persistenceLogManager != null) {
			persistenceLogManager.close();
		}
		
		if (upgradeTransaction != null) {
			 upgradeTransaction.commit();
			 upgradeTransaction = null;
		}
		if (persistenceUpgradeManager != null) {
			persistenceUpgradeManager.close();
		}
		
		if (flowTransaction != null) {
			 flowTransaction.commit();
			 flowTransaction = null;
		}
		if (persistenceFlowManager != null) {
			persistenceFlowManager.close();
		}
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#rollback()
	 */
	@Override
	public void rollback() {
		/** log is always committed */
		if (logTransaction != null) {
			logTransaction.commit();
			logTransaction = null;
		}
		if (persistenceLogManager != null) {
			persistenceLogManager.close();
		}
		
		if (upgradeTransaction != null) {
			 upgradeTransaction.rollback();
			 upgradeTransaction = null;
		}
		if (persistenceUpgradeManager != null) {
			persistenceUpgradeManager.close();
		}
		
		if (flowTransaction != null) {
			 flowTransaction.rollback();
			 flowTransaction = null;
		}
		if (persistenceFlowManager != null) {
			persistenceFlowManager.close();
		}
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#makePersistent(java.lang.Object)
	 */
	@Override
	public void makePersistent(final Object obj) {
//		if (obj instanceof ISystemLogsPersistence) {
//			if (logTransaction != null && persistenceLogManager != null) {
//				persistenceLogManager.makePersistent(obj);
//			}
//		}
		if (obj instanceof ISystemUpgradePersistence) {
			if (persistenceUpgradeManager != null) {
				persistenceUpgradeManager.makePersistent(obj);
			}
		}
		if (obj instanceof ISystemFlowPersistence) {
			if (persistenceFlowManager != null) {
				persistenceFlowManager.makePersistent(obj);
			}
		}
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#persistTransformer(java.lang.String, byte[])
	 */
	@Override
	public ICustomStepPersistence persistTransformer(String generateStorageName, byte[] remappedBytecode) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (persistenceUpgradeManager != null) {
			ICustomStepPersistence persist = null;
			try {
				persist = (ICustomStepPersistence) getSystemUpgradePersistence().getClassLoader().loadClass("chappy.persistence.datanucleus.upgrade.DatanucleusCustomStepPersistence").newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NullPointerException e ) {
				persist = (ICustomStepPersistence) ClassLoader.getSystemClassLoader().loadClass("chappy.persistence.datanucleus.upgrade.DatanucleusCustomStepPersistence").newInstance();
			}
			persist.setByteCode(remappedBytecode);
			persist.setStepName(generateStorageName);
			try {
				upgradeTransaction = persistenceUpgradeManager.currentTransaction();
				upgradeTransaction.begin();
				persistenceUpgradeManager.makePersistent(persist);
				upgradeTransaction.commit();
				upgradeTransaction  = null;
				return (ICustomStepPersistence) persist;
			} catch (Throwable e) {
				e.printStackTrace();
				upgradeTransaction.rollback();
				upgradeTransaction  = null;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#generateTransactionId(chappy.interfaces.cookies.CookieTransaction)
	 */
	@Override
	public void generateTransactionId(final CookieTransaction cookie) {
		// TODO Auto-generated method stub
		setTransactionId(cookie.getUserName());
		cookie.setTransactionId(getTransactionId());
	}
}
