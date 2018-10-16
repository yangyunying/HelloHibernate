package cn.test.entity;

public class Student {
	 private Integer sid;
	    private Integer age;
	    private String name;
	    public Integer getSid() {
	        return sid;
	    }
	    public void setSid(Integer sid) {
	        this.sid = sid;
	    }
	    public Integer getAge() {
	        return age;
	    }
	    public void setAge(Integer age) {
	        this.age = age;
	    }
	    public String getName() {
	        return name;
	    }
	    public void setName(String name) {
	        this.name = name;
	    }
	    @Override
	    public String toString() {
	        return "Student [sid=" + sid + ", age=" + age + ", name=" + name + "]";
	    }
}
