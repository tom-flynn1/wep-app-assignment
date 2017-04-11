/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.bean.Product;
import com.database.DataConnect;
import java.math.BigDecimal;

import javax.faces.bean.RequestScoped;
import org.apache.log4j.Logger;

@ManagedBean(name="product")
@RequestScoped
public class ProductBean implements Serializable{

	//resource injection
	@Resource(name="jdbc/users")
	private DataSource ds;
        private DataConnect dc;
        private String idParam = "-1";
        private String nameParam = "-1";
        private String quantityParam = "-1";
        private String priceParam = "-1";
        private Product searchResult;
        private Product newProduct;
        final static Logger logger = Logger.getLogger(ProductBean.class);
        
        public void test(){
            System.out.println("TESTING product bean");
        }

	//connect to DB and get customer list
	public List<Product> getProductList() throws SQLException
        {    
		if(ds==null)
			throw new SQLException("Can't get data source");

		//get database connection
		Connection con = dc.getConnection();

		if(con==null)
			throw new SQLException("Can't get database connection");

		PreparedStatement ps
			= con.prepareStatement(
			   "select * from app.products");

		//get customer data from database
		ResultSet result =  ps.executeQuery();

		List<Product> list = new ArrayList<Product>();

		while(result.next()){
			Product product = new Product();
                        
                        product.setId(result.getInt("ID"));
                        product.setPrice(result.getBigDecimal("PRICE"));
                        product.setProduct(result.getString("PRODUCT"));
                        product.setQuantity(result.getInt("QUANTITY"));

			//store all data into a List
			list.add(product);
		}

		return list;
	}
 
        //connect to DB and get customer list
	public String searchByID() throws SQLException{

            if(idParam.equalsIgnoreCase("-1") == false)
            {
		if(ds==null)
			throw new SQLException("Can't get data source");

		//get database connection
		Connection con = dc.getConnection();

		if(con==null)
			throw new SQLException("Can't get database connection");
                
               //ps = con.prepareStatement("SELECT USERNAME, PW, ISADMIN FROM APP.USERS WHERE USERNAME = ? AND PW = ?");

		PreparedStatement ps
			= con.prepareStatement(
			   "select * from app.products where id = ?");

		//get customer data from database 
                ps.setInt(1, Integer.parseInt(idParam));
                        
		ResultSet result =  ps.executeQuery();

		List<Product> list = new ArrayList<Product>();

		while(result.next()){
			Product product = new Product();
                        
                        product.setId(result.getInt("ID"));
                        product.setPrice(result.getBigDecimal("PRICE"));
                        product.setProduct(result.getString("PRODUCT"));
                        product.setQuantity(result.getInt("QUANTITY"));

			//store all data into a List
			list.add(product);
		}

                searchResult = list.get(0);
            }
            return "";
          //  return list;
	}
        
        //connect to DB and get customer list
	public String searchByName() throws SQLException{
            System.out.println("TESTING  searchByName() ");

            if(idParam.equalsIgnoreCase("-1") == false)
            {
		if(ds==null)
			throw new SQLException("Can't get data source");

		//get database connection
		Connection con = dc.getConnection();

		if(con==null)
			throw new SQLException("Can't get database connection");
                
               //ps = con.prepareStatement("SELECT USERNAME, PW, ISADMIN FROM APP.USERS WHERE USERNAME = ? AND PW = ?");

		PreparedStatement ps
			= con.prepareStatement(
			   "select * from app.products where product = ?");

		//get customer data from database 
                //ps.setInt(1, Integer.parseInt(idParam));
                ps.setString(1, nameParam);
                        
		ResultSet result =  ps.executeQuery();

		List<Product> list = new ArrayList<Product>();

		while(result.next()){
			Product product = new Product();
                        
                        product.setId(result.getInt("ID"));
                        product.setPrice(result.getBigDecimal("PRICE"));
                        product.setProduct(result.getString("PRODUCT"));
                        product.setQuantity(result.getInt("QUANTITY"));

			//store all data into a List
			list.add(product);
		}

                searchResult = list.get(0);
            }
            return "";
          //  return list;
	}
        
        public String updateQuantity(){
            
        Connection con = null;
        PreparedStatement ps = null;

        try{
            List<Product> productList = getProductList();

            for (Product p: productList)
            {
                con = DataConnect.getConnection();
                con.setAutoCommit(true);
                ps = con.prepareStatement("UPDATE APP.PRODUCTS SET QUANTITY = ? WHERE ID = ?");
                
                System.out.println("testing 1");
                System.out.println("quantityParam = " + quantityParam);
                System.out.println("id Param = " + idParam);
                    
                if(p.getId().equals(Integer.parseInt(idParam)))
                {
                    //System.out.println("updateProduct.getQuantity() = " + updateProduct.getQuantity());
                   
                    int updateQuantity = Integer.parseInt(quantityParam);
                    ps.setInt(1, updateQuantity);
                    ps.setInt(2, p.getId());
                    ps.execute();
                    con.commit();
                }
            }  
        }
        catch(Exception e){
            e.printStackTrace();
        }
    
            return "";
        }
        
    public String addNewProduct(){
        
        Connection con = null;
        PreparedStatement ps = null;

        try{
            List<Product> productList = getProductList();

            for (Product p: productList)
            {
                con = DataConnect.getConnection();
                con.setAutoCommit(true);
                ps = con.prepareStatement("INSERT INTO APP.PRODUCTS (ID, PRODUCT, QUANTITY, PRICE) VALUES (?,?,?,?)");
                
                int id = Integer.parseInt(idParam);
                ps.setInt(1, id);
                ps.setString(2, nameParam);
                ps.setInt(3, Integer.parseInt(quantityParam));
                //int value = Integer.parseInt(priceParam);
                double val = Double.parseDouble(priceParam);
                ps.setDouble(4, val);
                ps.execute();
                con.commit();   
                
                logger.info("Product : " + nameParam + " added to inventory");
            }  
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return "";
    }
    
    public String removeProduct(Product removed){
        Connection con = null;
        PreparedStatement ps = null;

        try
        {
            con = DataConnect.getConnection();
            con.setAutoCommit(true);
            ps = con.prepareStatement("DELETE FROM APP.PRODUCTS WHERE ID =?");

            int id = removed.getId();
            ps.setInt(1, id);     
            ps.execute();
            con.commit();   

            logger.info("Product : " + removed.getProduct() + " removed from inventory");

        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return "";
    }

    public Product getNewProduct() {
        return newProduct;
    }

    public void setNewProduct(Product newProduct) {
        this.newProduct = newProduct;
    }

        

    public String getIdParam() {
        return idParam;
    }

    public void setIdParam(String idParam) {
        System.out.println("TESTING  setIdParam ");
        this.idParam = idParam;
    }

    public String getNameParam() {
        return nameParam;
    }

    public void setNameParam(String nameParam) {
        System.out.println("TESTING  setNameParam ");
        this.nameParam = nameParam;
    }

    public Product getResult() {
        return searchResult;
    }

    public void setResult(Product result) {
        this.searchResult = result;
    }    

    public String getQuantityParam() {
        return quantityParam;
    }

    public void setQuantityParam(String quantityParam) {
        this.quantityParam = quantityParam;
    }

    public String getPriceParam() {
        return priceParam;
    }

    public void setPriceParam(String priceParam) {
        this.priceParam = priceParam;
    } 
    
}
