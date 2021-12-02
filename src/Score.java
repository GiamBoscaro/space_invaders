import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Score {
	
	DBManager man;
	DateFormat dateFormat;
	Calendar cal;
	
	public Score(){
		
		man = new DBManager();
		man.addUrl("src/database/score.mdb");		
	}
	
	public void reset(){	
		
		man.openConnection();
		
		man.query("DELETE FROM Scoreboard");
		
		man.closeConnection();
	}
	
	public String getScores() throws SQLException{
		
		String str = "";
		int rows = 0;
		
		man.openConnection();		
		
		ResultSet rs = man.query("SELECT User, Score, ScoreDate  FROM Scoreboard ORDER BY Score DESC");	
		rs.last();		
		rows = rs.getRow();
		rs.beforeFirst();
		
		if(rows != 0){	
			while(rs.next())
				str = str + rs.getString("User")+","+rs.getInt("Score")+","+rs.getString("ScoreDate")+"%";	
			
			str = str.substring(0, str.length()-1);
		}
		
		rs.close();
		man.closeConnection();
		
		return str;
	}

	public int getRecord() throws SQLException{
		
		int record = 0;		
		int rows = 0;
		
		man.openConnection();
		
		ResultSet rs = man.query("SELECT MAX(Score) AS Max FROM Scoreboard");	
		rs.last();		
		rows = rs.getRow();
		rs.first();
		
		if(rows != 0)			
			record = Integer.parseInt(rs.getString("Max"));	 
		
		rs.close();
		man.closeConnection();
		
		return record;		
	}
	
	public void addScore(int score, String user) throws SQLException{
		
		dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		cal = Calendar.getInstance();
		
		man.openConnection();

		man.update("INSERT INTO Scoreboard (User, Score, ScoreDate) VALUES ('"+user+"',"+score+",'"+dateFormat.format(cal.getTime())+"')");	

		man.closeConnection();
	
	}
}
