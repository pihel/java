import org.apache.commons.lang.SystemUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class HdfsReadWrite {
	FileSystem fs = null;
	//download from https://github.com/srccodes/hadoop-common-2.2.0-bin/tree/master/bin
	String home_dir = "C:\\winutils"; 
	
	HdfsReadWrite(String hdfsUrl, String hdfsUser) throws IOException, URISyntaxException {
		//disable warning and info message
		Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.ERROR);
		
		if(SystemUtils.IS_OS_WINDOWS) {
			System.setProperty("hadoop.home.dir", home_dir);
		}
		System.setProperty("HADOOP_USER_NAME", hdfsUser);
		
		Configuration configuration = new Configuration();
		
		fs = FileSystem.get(new URI(hdfsUrl), configuration);
	} //HdfsReadwrite
	
	String readTextFile(String file, Integer lines) throws IOException {
		String str = "";
		
		Path srcPath = new Path(file);
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(fs.open(srcPath)));
		String line;
		int ln = 0;
		while ((line=  br.readLine()) != null) {
			str = str + line + "\n";
			
			ln++;
			if(lines > 0 && ln >= lines) {
				break;
			}
		}
		br.close();
		
		return str;
	} //readTextFile
	
	void writeText(String text, String hdfs_file) throws IOException {
		Path hdfswritepath = new Path(hdfs_file);
		FSDataOutputStream outputStream = fs.create(hdfswritepath);
		outputStream.writeBytes(text);
		//outputStream.writeUTF(text);
		outputStream.close();
	} //writeText
	
	void writeFile(String local_file, String hdfs_file, String local_encode) throws IOException {
		Path hdfswritepath = new Path(hdfs_file);
		FSDataOutputStream outputStream = fs.create(hdfswritepath);		
		
		//BufferedReader br = new BufferedReader(new FileReader(local_file));
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(local_file), local_encode));
	    String line;
	    
	    long lines = 0;
	    long divide = 10;
	    System.out.println("Copying...");
	    while ((line = br.readLine()) != null) {
	    	outputStream.write(line.getBytes(StandardCharsets.UTF_8));
	    	outputStream.writeBytes("\n");
	    	lines++;
	    	
	    	if(lines % divide == 0) {
	    		System.out.println(lines);
	    		divide = divide * 10;
	    	}
	    }
	    System.out.println("Total lines: " + lines);
	    outputStream.close();
	    br.close();
	} //writeFile
	
	void mkDir(String dir) throws IOException {
	    Path newFolderPath = new Path(dir);
	    if(!fs.exists(newFolderPath)) {
	    	fs.mkdirs(newFolderPath);
	    }
	} //mkDir
	
	void rmDir(String dir) throws IOException {
	    Path newFolderPath = new Path(dir);
	    if(fs.exists(newFolderPath)) {
	    	fs.delete(newFolderPath, true);
	    }
	} //rmDir
} //HdfsReadWrite

class HiveSql {
	FileSystem fs = null;
	String home_dir = "C:\\winutils"; 
	Connection dbcon;
	Statement stmt;
	
	HiveSql(String con, String login, String pasw) throws IOException, URISyntaxException, SQLException {
		//disable warning and info message
		Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.ERROR);
		
		if(SystemUtils.IS_OS_WINDOWS) {
			System.setProperty("hadoop.home.dir", home_dir);
		}
		
		try {
	      Class.forName("org.apache.hive.jdbc.HiveDriver");
	    } catch (ClassNotFoundException e) {
	      e.printStackTrace();
	      System.exit(1);
	    }		
		
		dbcon = DriverManager.getConnection(con, login, pasw);
	} //HiveSql
	
	void exec(String sql) throws SQLException {
		if(stmt != null && !stmt.isClosed()) stmt.close();
		stmt = dbcon.createStatement();
		stmt.execute(sql);
		if(stmt != null && !stmt.isClosed()) stmt.close();
	} //exec
	
	
	ResultSet select(String sql) throws SQLException {
		if(stmt != null && !stmt.isClosed()) stmt.close();
		stmt = dbcon.createStatement();
		return stmt.executeQuery(sql);
	} //select
	
	protected void finalize() {
		try {
			stmt.close();
			dbcon.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
} //HiveSql
