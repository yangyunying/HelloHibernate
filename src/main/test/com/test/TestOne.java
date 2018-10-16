package com.test;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import cn.test.entity.HibernateUtil;
import cn.test.entity.Student;

public class TestOne {
	
    Session session;
    Transaction tx; 
    
    /*
     * get方法查询
     */
    public void getData(){
        Student stu=(Student)session.get(Student.class, 3);
        System.out.println(stu);
    }
    
    /*
     * 增加
     */
    public void addData(){
        Student stu=new Student();
        //stu.setSid(12);
        stu.setAge(16);
        stu.setName("子衿");
        //读取大配置文件 获取连接信息
        Configuration cfg=new Configuration().configure();
    
        //创建SessionFactory
        SessionFactory fa=cfg.buildSessionFactory();
	    //加工Session
	    Session se=fa.openSession();
	    Transaction tx = se.beginTransaction();
	    //保存
	    se.save(stu);
	    //事务提交
	    tx.commit();
	    se.close();
	    
	    System.out.println("Save ok!");
    }
     
    /*
     * 删除
     */
    public void delData(){
        Session session=HibernateUtil.getSession();
        Student stu=new Student();
        stu.setSid(2);
        Transaction tx=session.beginTransaction();
        session.delete(stu);
        tx.commit();
        HibernateUtil.CloseSession();
        System.out.println("del ok!");
    }
    
    /*
     * 修改
     */
    public void updateData(){
        Session session=HibernateUtil.getSession();        
        Student stu=(Student)session.load(Student.class,3);
        stu.setName("呵呵");
        Transaction tx=session.beginTransaction();
        session.update(stu);
        tx.commit();
        HibernateUtil.CloseSession();
        System.out.println("update ok!");
    }
    
    //查所有
    public void getList(){
        Session session=HibernateUtil.getSession();        
        //此时会发出一条sql，将30个学生全部查询出来
        List<Student> ls = (List<Student>)session.createQuery("from Student")
                            .setFirstResult(0).setMaxResults(30).list();
        Iterator<Student> stus = ls.iterator();
        for(;stus.hasNext();)
        {
            Student stu = (Student)stus.next();
            System.out.println(stu.getName());
        }
        
    }
    
    //查所有iterator
    public void getListByIterator(){
        Session session=HibernateUtil.getSession();        
        /**
         * 如果使用iterator方法返回列表，对于hibernate而言，它仅仅只是发出取id列表的sql
         * 在查询相应的具体的某个学生信息时，会发出相应的SQL去取学生信息
         * 这就是典型的N+1问题
         * 存在iterator的原因是，有可能会在一个session中查询两次数据，如果使用list每一次都会把所有的对象查询上来
         * 而是要iterator仅仅只会查询id，此时所有的对象已经存储在一级缓存(session的缓存)中，可以直接获取
         */
        Iterator<Student> stus = (Iterator<Student>)session.createQuery("from Student")
                            .setFirstResult(0).setMaxResults(30).iterate();
        for(;stus.hasNext();)
        {
            Student stu = (Student)stus.next();
            System.out.println(stu.getName());
        }
        
    }
    
    //测试一级缓存
    public void testFirstLevelSession(){
    	Session session=HibernateUtil.getSession();      
    	/**
         * 此时会发出一条sql，将所有学生全部查询出来，并放到session的一级缓存当中
         * 当再次查询学生信息时，会首先去缓存中看是否存在，如果不存在，再去数据库中查询
         * 这就是hibernate的一级缓存(session缓存)
         */
        List<Student> stus = (List<Student>)session.createQuery("from Student")
                                .setFirstResult(0).setMaxResults(30).list();
        Student stu = (Student)session.load(Student.class, 1);
    	System.out.println(stu);
    }
    
    //测试一级缓存作用范围
    public void testFirstLevelSessionScope(){
    	try
        {
    		Session session = HibernateUtil.getSession();
            
            /**
             * 此时会发出一条sql，将所有学生全部查询出来，并放到session的一级缓存当中
             * 当再次查询学生信息时，会首先去缓存中看是否存在，如果不存在，再去数据库中查询
             * 这就是hibernate的一级缓存(session缓存)
             */
            List<Student> stus = (List<Student>)session.createQuery("from Student")
                                    .setFirstResult(0).setMaxResults(30).list();
            Student stu = (Student)session.load(Student.class, 1);
            System.out.println(stu.getName() + "-----------");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            HibernateUtil.CloseSession();
        }
        /**
         * 当session关闭以后，session的一级缓存也就没有了，这时就又会去数据库中查询
         */
        session = HibernateUtil.getSession();
        Student stu = (Student)session.load(Student.class, 1);
        System.out.println(stu.getName() + "-----------");
    }
    
    
    public static void main(String[] args) {
    	TestOne t = new TestOne();
//    	t.getList();
//    	System.out.println("--------------------------");
//    	t.getListByIterator();
    	
    	
    	t.testFirstLevelSession();
    	System.out.println("--------------------------");
    	t.testFirstLevelSessionScope();
	}

}