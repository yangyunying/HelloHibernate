package com.test;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import cn.test.entity.HibernateUtil;
import cn.test.entity.Student;

public class TestSecondCache {
	 public void testSecondLevelCache()
	    {
	        Session session = null;
	        try
	        {
	            session = HibernateUtil.getSession();

	            Student stu = (Student) session.load(Student.class, 1);
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
	        try
	        {
	            /**
	             * 即使当session关闭以后，因为配置了二级缓存，而二级缓存是sessionFactory级别的，所以会从缓存中取出该数据
	             * 只会发出一条sql语句
	             */
	            session = HibernateUtil.getSession();
	            Student stu = (Student) session.load(Student.class, 1);
	            System.out.println(stu.getName() + "-----------");
	            /**
	             * 因为设置了二级缓存为read-only，所以不能对其进行修改
	             */
	            session.beginTransaction();
	            stu.setName("aaa");
	            session.beginTransaction().commit();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	            session.beginTransaction().rollback();
	        }
	        finally
	        {
	            HibernateUtil.CloseSession();
	        }
	    }
	 
	 
	 /**
	  * 需要查询出两次对象的时候，可以使用二级缓存来解决N+1的问题
	  */
	 public void testCache3()
	    {
	        Session session = null;
	        try
	        {
	            session = HibernateUtil.getSession();
	            /**
	             * 将查询出来的Student对象缓存到二级缓存中去
	             */
	            List<Student> stus = (List<Student>) session.createQuery(
	                    "select stu from Student stu").list();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        finally
	        {
	            HibernateUtil.CloseSession();
	        }
	        try
	        {
	            /**
	             * 由于学生的对象已经缓存在二级缓存中了，此时再使用iterate来获取对象的时候，首先会通过一条
	             * 取id的语句，然后在获取对象时去二级缓存中，如果发现就不会再发SQL，这样也就解决了N+1问题 
	             * 而且内存占用也不多
	             */
	            session = HibernateUtil.getSession();
	            Iterator<Student> iterator = session.createQuery("from Student")
	                    .iterate();
	            for (; iterator.hasNext();)
	            {
	                Student stu = (Student) iterator.next();
	                System.out.println(stu.getName());
	            }
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	 
	 
	 /**
	  * 二级缓存不会缓存我们的hql查询语句，要想解决这个问题，我们就要配置我们的查询缓存了
	  */
	 public void testHQLQuery()
	    {
	        Session session = null;
	        try
	        {
	            session = HibernateUtil.getSession();
	            List<Student> ls = session.createQuery("from Student")
	                    .setFirstResult(0).setMaxResults(50).list();
	            System.out.println("--------------------------");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        finally
	        {
	            HibernateUtil.CloseSession();
	        }
	        try
	        {
	            /**
	             * 使用List会发出两条一模一样的sql，此时如果希望不发sql就需要使用查询缓存
	             */
	            session = HibernateUtil.getSession();
	            List<Student> ls = session.createQuery("from Student")
	                    .setFirstResult(0).setMaxResults(50).list();
	            Iterator<Student> stu = ls.iterator();
	            for(;stu.hasNext();)
	            {
	                Student student = stu.next();
	                System.out.println(student.getName());
	            }
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        finally
	        {
	        	HibernateUtil.CloseSession();
	        }
	    }
	 
	 /**
	  * 1、<!-- 开启查询缓存,在hibernate.cfg.xml中加入一条配置即可 -->
        <property name="hibernate.cache.use_query_cache">true</property>
        
        
        2、HQL也能引起N+1问题，解决方案开启二级缓存
	  */
	 public void testHQLQueryOpen() {
	        Session session = null;
	        try {
	            /**
	             * 此时会发出一条sql取出所有的学生信息
	             */
	            session = HibernateUtil.getSession();
	            List<Student> ls = session.createQuery("from Student")
	                    .setCacheable(true)  //开启查询缓存,查询缓存也是sessionFactory级别的缓存
	                    .setFirstResult(0).setMaxResults(50).list();
	            Iterator<Student> stus = ls.iterator();
	            for(;stus.hasNext();) {
	                Student stu = stus.next();
	                System.out.println(stu.getName());
	            }
	            System.out.println("--------------------------------");
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            HibernateUtil.CloseSession();
	        }
	        try {
	            /**
	             * 此时会发出一条sql取出所有的学生信息
	             */
	            session = HibernateUtil.getSession();
	            List<Student> ls = session.createQuery("from Student")
	                    .setCacheable(true)  //开启查询缓存,查询缓存也是sessionFactory级别的缓存
	                    .setFirstResult(0).setMaxResults(50).list();
	            Iterator<Student> stus = ls.iterator();
	            for(;stus.hasNext();) {
	                Student stu = stus.next();
	                System.out.println(stu.getName());
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            HibernateUtil.CloseSession();
	        }
	    }
	 
	 public static void main(String[] args) {
		TestSecondCache t2 = new TestSecondCache();
		//t2.testSecondLevelCache();
		//t2.testCache3();
		
		//t2.testHQLQuery();
		//System.out.println("*********************");
		t2.testHQLQueryOpen();
	}
}
