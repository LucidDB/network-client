/*
//Copyright (C) 2010 DynamoBI Corporation
//
//This library is free software; you can redistribute it and/or modify it
//under the terms of the GNU Lesser General Public License as published by
//the Free Software Foundation; either version 2.1 of the License, or (at
//your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this library; if not, write to the Free Software
//Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
*/
package com.dynamobi.network.client;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class DynamoNetworkUDR
{
	
	public static String PLUGIN_DIR = "plugin";
	final static int size=1024;
 
  public static void install(
		  String jarURL
  )	
     throws Exception
 {
	  String pluginDir = System.getProperty("net.sf.farrago.home") + "/" + PLUGIN_DIR;
	  System.out.println("pluginDir: " + pluginDir);
	  
	  System.out.println("Installing jarURL" + jarURL);
	  String jarName = null;
	  String schemaName = null;
	  
	  Connection conn = null;
	  PreparedStatement prep = null;
	  ResultSet rs = null;
	  
	  try {
		  conn = DriverManager.getConnection("jdbc:default:connection");
		  prep = conn.prepareStatement("select URL, JARNAME, SCHEMA_NAME FROM dynamonetwork.network_plugins WHERE URL=?");
		  prep.setString(1,jarURL);
		  rs = prep.executeQuery();
		  if (rs.next()) {
		      jarName = rs.getString(2);
		      schemaName = rs.getString(3);
		          
		  }
		  else {
			  throw new Exception ("URL not in Network");
		  }
		  
		  // We now have jarURL, jarName, and schemaName
		  System.out.println( "jarURL: " + jarURL + " - jarName: " + jarName + " - schemaName: " + schemaName);
		  
		  retrieveFile(jarURL, pluginDir + "/" + jarName + ".jar");
		  
		  // create or replace our schema
		  conn.createStatement().execute("create or replace schema " + schemaName);
		  
		  // install jar
		  String installSql = "create jar " + schemaName + "." + jarName 
		  	+ " library 'file:" + pluginDir + "/" + jarName + ".jar'"
		  	+ " options (1)";
		  	
		  conn.createStatement().execute(installSql);
		  
		  
		  
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	finally {
		rs.close();
	}
	  
      
      
	  
     
  }
 
  public static void retrieveFile (String URL, String localFile){
	  

	  OutputStream outStream = null;
	  URLConnection  uCon = null;

	  InputStream is = null;
	  try {
	  URL Url;
	  byte[] buf;
	  int ByteRead,ByteWritten=0;
	  Url= new URL(URL);

	  outStream = new BufferedOutputStream(new
	  FileOutputStream(localFile));

	  uCon = Url.openConnection();
	  is = uCon.getInputStream();
	  buf = new byte[size];
	  while ((ByteRead = is.read(buf)) != -1) {
	  outStream.write(buf, 0, ByteRead);
	  ByteWritten += ByteRead;
	  }
	  }
	  catch (Exception e) {
	  e.printStackTrace();
	  }
	  finally {
	  try {
	  is.close();
	  outStream.close();
	  }
	  catch (IOException e) {
	  e.printStackTrace();
	  }}


	 

 
  }
 
  
  
}