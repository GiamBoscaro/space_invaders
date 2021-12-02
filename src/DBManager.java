import java.sql.*;

public class DBManager{
	
	String url= "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ=Data.mdb;";
	Connection conn;         
	
	public void addUrl(String str){
		
		url = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ="+str+";";
	}	

	public ResultSet query(String query){
		
	    try{
	    	
	        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
	        ResultSet rs = stmt.executeQuery(query);
	        return rs;
	        
	    }catch(SQLException e){
	    	
	    	System.out.println("Query Error: "+e.getMessage());
	        closeConnection();
	        return null;
	    }
	}
	public void update(String update){
		
	    try{
	    	
	        Statement stmt = conn.createStatement();
	        stmt.execute(update);
	        
	    }catch(SQLException e){
	    	
	    	System.out.println("Update Error: "+e.getMessage());
	        closeConnection();
	    }
	}
	
	public void openConnection(){
		
	    try{
	    	
	        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
	        
	    }catch(ClassNotFoundException e){
	    	
	    	System.out.println("Driver Error: "+e.getMessage());
	    	
	    }try{
	    	
	        conn = DriverManager.getConnection(url);
	        
	    }catch(SQLException e){
	    	
	    	System.out.println("Connection Error: "+e.getMessage());
	    }
	}
	
	public void closeConnection(){
		
	    try{
	    	
	        conn.close();
	        
	    }catch(SQLException e){
	    	
	    	System.out.println("Close Error: "+e.getMessage());
	    }
	}
}