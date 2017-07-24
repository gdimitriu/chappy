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

import chappy.interfaces.markers.ISystemFlowPersistence;
import chappy.interfaces.markers.ISystemLogsPersistence;
import chappy.interfaces.markers.ISystemUpgradePersistence;
import chappy.persistence.datanucleus.upgrade.DatanucleusCustomStepPersistence;
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
				if (persistenceUpgradeManager != null) {
					upgradeTransaction = persistenceUpgradeManager.currentTransaction();
					upgradeTransaction.begin();
				}
			}
		}
		if (flowTransaction == null) {
			if (getSystemFlowPersistence() != null) {
				persistenceFlowManager  = ((PersistenceManagerFactory) getSystemFlowPersistence().getFactory()).getPersistenceManager();
				if (persistenceFlowManager != null) {
					flowTransaction = persistenceFlowManager.currentTransaction();
					flowTransaction.begin();
				}
			}
		}
	}

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

	@Override
	public void makePersistent(final Object obj) {
		if (obj instanceof ISystemLogsPersistence) {
			if (logTransaction != null && persistenceLogManager != null) {
				persistenceLogManager.makePersistent(obj);
			}
		} else if (obj instanceof ISystemUpgradePersistence) {
			if (upgradeTransaction != null && persistenceUpgradeManager != null) {
				persistenceUpgradeManager.makePersistent(obj);
			}
		} else if (obj instanceof ISystemFlowPersistence) {
			if (flowTransaction != null && persistenceFlowManager != null) {
				persistenceFlowManager.makePersistent(obj);
			}
		}
	}

	@Override
	public void persistTransformer(String generateStorageName, byte[] remappedBytecode) {
		if (upgradeTransaction != null && persistenceUpgradeManager != null) {
			DatanucleusCustomStepPersistence persist = new DatanucleusCustomStepPersistence();
			persist.setByteCode(remappedBytecode);
			persist.setStepName(generateStorageName);
			try {
				persistenceUpgradeManager.makePersistent(persist);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
