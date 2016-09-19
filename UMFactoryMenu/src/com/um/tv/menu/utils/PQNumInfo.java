package com.um.tv.menu.utils;


public class PQNumInfo {
	private String id;
	
	private String describe;
	
	private String project_id;

	private String panelindex; 

	private String   pqvalue;
	
	private String   pqsrc;
	
	private String   pqdes;

	private String  inivalue;
	
	private String  inisrc;
	
	private String  inides;

	public String getProject_id() {
		return project_id;
	}

	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}

	public String getPanelindex() {
		return panelindex;
	}

	public void setPanelindex(String panelindex) {
		this.panelindex = panelindex;
	}



	public String toString(){
		return   "id:"+id+",project_id:"+project_id+",panelindex:"+panelindex+",pqname:"
				 +pqvalue+pqsrc+pqdes+",ininame:"+inivalue+inisrc+inides;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPqvalue() {
		return pqvalue;
	}

	public void setPqvalue(String pqvalue) {
		this.pqvalue = pqvalue;
	}

	public String getPqsrc() {
		return pqsrc;
	}

	public void setPqsrc(String pqsrc) {
		this.pqsrc = pqsrc;
	}

	public String getPqdes() {
		return pqdes;
	}

	public void setPqdes(String pqdes) {
		this.pqdes = pqdes;
	}

	public String getInivalue() {
		return inivalue;
	}

	public void setInivalue(String inivalue) {
		this.inivalue = inivalue;
	}

	public String getInisrc() {
		return inisrc;
	}

	public void setInisrc(String inisrc) {
		this.inisrc = inisrc;
	}

	public String getInides() {
		return inides;
	}

	public void setInides(String inides) {
		this.inides = inides;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}
}
