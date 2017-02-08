package com.c2point.tools.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBupdate {
		private static Logger logger = LogManager.getLogger( DBupdate.class.getName());

		private String dbName = null;
		private String usrname = null;
		private String pwd = null;
		private Connection con = null;

		public DBupdate() {}

		public static void updateDatabase() {

			logger.debug( "Start Update Database..." );
			
			DBupdate updater = new DBupdate();
			updater.readParameters();
			
			// get connection. exit if con == null
			if ( updater.initConnection() == null ) {
				logger.error( "Cannot open connection to DB! Exit." );
				System.exit( 0 );
			}
			
			// Create 'config' table if missing. Setup db ver 1
			updater.createConfigTableIfNecessary();
			
			// Change DB depending on current db version
			long db_version = updater.getDbVersion();
			
			boolean res = false;
			if ( db_version == 1 ) {
//				res = updater.convert_from_1_to_2();
			} else if ( db_version == 2 ) {
			} else if ( db_version == 3 ) {
			} else if ( db_version == 4 ) {
			} else if ( db_version == 5 ) {
			} else if ( db_version == 6 ) {
			} else if ( db_version == 7 ) {
			} else if ( db_version == 8 ) {
			} else if ( db_version == 9 ) {
			} else if ( db_version == 10 ) {
				// Holder for the future
			} else if ( db_version == 11 ) {
				// Holder for the future
			} else if ( db_version == 12 ) {
				// Holder for the future
			} else if ( db_version == 13 ) {
				// Holder for the future
			} else if ( db_version == 14 ) {
				// Holder for the future
			} else if ( db_version == 15 ) {
				// Holder for the future
			} else {
				logger.debug( "  Update NOT necessary!" );
			}
			
			if ( res ) {
//				updater.setupNextVersion( db_version );
			}
		
			updater.closeConnection();

			logger.debug( "... end Update Database" );
		}
		
		private void readParameters() {
			this.dbName = Configuration.getProperty( "javax.persistence.jdbc.url", "tms" );
			this.usrname = Configuration.getProperty( "javax.persistence.jdbc.user", "tms" );
			this.pwd = Configuration.getProperty( "javax.persistence.jdbc.password", "tms" );
			logger.debug( "Parameters were read successfully: " + this.dbName + ", " + this.usrname + ", " + this.pwd );
		}

		
		private void createConfigTableIfNecessary() {
			String stmtStr;
				
			stmtStr = "select * from configuration";
			if ( executeQuery( stmtStr ) == null ) {
				// Table must be created
				logger.debug( "Table 'configutration must be created" );
				
				stmtStr = "create table configuration ( " 
							+ "id bigint NOT NULL, "
							+ "db_version bigint, "
							+ "PRIMARY KEY (id) "
							+ " )";
			
				if ( executeUpdate( stmtStr )) {
					logger.debug( "'configuration' table has been created" );
				}

				// Initial version shall be provisioned
				logger.debug( "Initial version shall be provisioned" );
				
				stmtStr = "insert into configuration values ( "
			            	+ " 1, 1 "
			            	+ ")";
	 
		
				if ( executeUpdate( stmtStr )) {
					logger.debug( "'configuration' table has been provisiioned to initial values" );
				}
				
			}
			
			
		}
		
		public long getDbVersion() {
			long lRes = -1;
			String stmtStr;
			Statement stmt = null;
			ResultSet rs = null;
				
			stmtStr = "select * from configuration";
			
			try {
				stmt = con.createStatement();
				rs = stmt.executeQuery( stmtStr);
				if ( rs != null && rs.next()) {
					lRes = rs.getLong( 2 );
				}
				logger.debug( "DB version was fetched. db_version = " + lRes );
				
			} catch ( SQLException e ) {
				logger.error( "Cannot fetch db version from the record!\n" + e );
			} finally {
				try {
					rs.close();
					stmt.close();
				} catch (SQLException e) {
				}
			}
			
			return lRes;
		}

		private void setupNextVersion( long db_version ) {
			String stmtStr;
			Statement stmt = null;
			ResultSet rs = null;
				
			stmtStr = "select * from configuration";
			
			try {
				stmt = con.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE );
				rs = stmt.executeQuery( stmtStr );
				if ( rs != null && rs.next()) {
					rs.updateLong( 2, db_version + 1 );
		            rs.updateRow();
					logger.debug( "DB version will be = " + (db_version + 1));
				}

				
			} catch ( SQLException e ) {
				logger.error( "Cannot fetch db version from the record!\n" + e );
			} finally {
				try {
					rs.close();
					stmt.close();
				} catch (SQLException e) {
				}
			}
			
		}
		
		private Connection initConnection() {
			
			try {
				Class.forName( "org.postgresql.Driver" );
				logger.debug( "Driver was loaded" );
			} catch (Exception x) {
				logger.error( x );
				logger.error( "Failed to load the driver" );
				return null;
			}
			
			try {
				con = DriverManager.getConnection( "jdbc:postgresql:" + dbName, usrname, pwd );
				logger.debug( "Connection has been obtained!" );
			} catch (SQLException e) {
				logger.error( dbName + " database does not exist!" );
				return null;
			}
			
			return con;
		}
		private void closeConnection() {
			if ( con != null ) {
				try {
					con.close();
					logger.debug( "Connection has been closed!" );
				} catch (SQLException e) {
					logger.error( e );
					logger.error( "Failed to close connection" );
				}
			}
			
		}
		private ResultSet executeQuery( String stmtStr ) {
			ResultSet rs = null;
			
			Statement stmt = null;

			try {
				stmt = con.createStatement();
				rs = stmt.executeQuery( stmtStr);
				logger.debug( "executeQuery '" + stmtStr + "' succeeded" );
			} catch ( SQLException e ) {
				logger.debug( "executeQuery '" + stmtStr + "' failed:\n" + e );
			} finally {
				try {
					if ( stmt != null )
						stmt.close();
				} catch (SQLException e) {
				}
			}
			
			return rs;
		}

		private boolean executeUpdate( String stmtStr ) {
			boolean bRes = false;
			
			Statement stmt = null;

			try {
				stmt = con.createStatement();
				stmt.executeUpdate( stmtStr);
				bRes = true;
				logger.debug( "executeUpdate '" + stmtStr + "' succeeded" );
			} catch ( SQLException e ) {
				logger.error( "executeUpdate '" + stmtStr + "' failed:\n" + e );
			} finally {
				try {
					if ( stmt != null )
						stmt.close();
				} catch (SQLException e) {
				}
			}
			
			return bRes;
		}
		
		/*
		 * Conversion from version 1 (initial) to version 2:
		 *   - Add property string field to Organisation 
		 *     
		 */
		private boolean convert_from_1_to_2() {
			boolean res = false;

			String stmtStr;

			// 1. Update Organisation table
			//		- Add column to save properties
		    
			stmtStr = "ALTER TABLE organisation "
					+ "ADD COLUMN propstring character varying(4096)";
			 
			
			if ( executeUpdate( stmtStr )) {
				logger.debug( "'Organisation' table has been updated successfully" );
				res = true;
			} else {
				logger.error( "'Organisation' table has NOT been updated successfully!" );
				return false;
			}
			
			// 2. Tool models move to separate table
			//		
			
			// Create table 'Models' (id, Model)
			// Add to 'Tools' column model_id with foreign key referensed to models( id )
			// 

			
			
			return true;
		}
		
	}
