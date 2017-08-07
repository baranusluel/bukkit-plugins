package net.choicecraft.ChoiceWorks.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.choicecraft.ChoiceWorks.ChoiceWorks;

public class CDatabaseResult implements Iterable<Map<String, String>> {
	private int m_ColumnCount;
	private int m_RowCount;
	
	private List<Map<String, String>> m_RowData = new ArrayList<Map<String, String>>();
	
	/**
	 * <b>[Internal ChoiceWorks Function - Do Not Use]</b><br>
	 * NULL constructor for worst-case queries.
	 */
	CDatabaseResult() {
		m_ColumnCount = 0;
		m_RowCount = 0;
	}
	
	/**
	 * <b>[Internal ChoiceWorks Function - Do Not Use]</b><br>
	 * SQL Exception constructor to store error information.
	 * @param e The SQL Exception handle.
	 */
	CDatabaseResult(SQLException e) {
		m_ColumnCount = 0;
		m_RowCount = 0;
		
		ChoiceWorks.print(e.getMessage(), "CDatabase");
	}
	
	/**
	 * <b>[Internal ChoiceWorks Function - Do Not Use]</b><br>
	 * Update constructor to save updated rows count.
	 * @param updateCount The amount of updated rows.
	 */
	CDatabaseResult(int updateCount) {
		m_ColumnCount = 0;
		m_RowCount = updateCount;
	}

	CDatabaseResult(ResultSet results) throws SQLException {
		// warning: this ResultSet is only available here, so get what we need!
		ResultSetMetaData metadata = results.getMetaData();
		
		// get the column count from the table metadata:
		m_ColumnCount = metadata.getColumnCount();
		m_RowCount = 0;
		
		// iterate through all result fields:
		while (results.next()) {
			Integer count = 0;
			Map<String, String> entry = new HashMap<String, String>();
	        while(count+1 <= m_ColumnCount) {
	        	count++;
	        	String value = results.getString(count);	// protect against NULL
	        	if(value == null) value = "";				// protect against NULL
	        	entry.put(metadata.getColumnName(count), value);
	        }
	        m_RowData.add(entry);
	        m_RowCount++;
		}
	}

	public int getColumnCount() {
		return m_ColumnCount;
	}

	public int getRowCount() {
		return m_RowCount;
	}
	
	public List<Map<String, String>> getData() {
		return m_RowData;
	}

	@Override
	public Iterator<Map<String, String>> iterator() {
		return m_RowData.iterator();
	}
	
}
