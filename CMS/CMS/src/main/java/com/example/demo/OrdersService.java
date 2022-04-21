package com.example.demo;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

@Service
@Transactional
public class OrdersService {
	

	    @Autowired
	    private OrdersRepository repo;
	    
	    @Autowired
	    private JavaMailSender javaMailSender;
	   
	    @Autowired
	    private OrdersDAO dao;
	    
	    @Autowired
	    private CustomerDAO cdao;
	    
	    @Autowired
	    private CustomerService scus;
	    
	    @Autowired 
	    private MenuDAO mdao;
	    
	    @Autowired 
	    private WalletDAO wdao;
	    public String placeOrder(Orders order) {
	    	order.setOrd_id(dao.generateordid());
	    	Customer cust=scus.get(order.getCus_id());
	        Menu menu = mdao.searchMenu(order.getMen_id());
	        String email=cdao.getMailId(order.getCus_id());
	        Wallet wallet = wdao.showCustomerWallet(order.getCus_id(), order.getWal_source());
	        double balance = wallet.getWal_amount();
	        double billAmount = order.getOrd_quantity()*menu.getMen_price();
	        System.out.println(balance);
	        System.out.println(billAmount);
	        if (balance-billAmount > 0) {
	            order.setOrd_status("PENDING");
	            order.setOrd_billamount(order.getOrd_quantity()*menu.getMen_price());
	            repo.save(order);
	            wdao.updateWallet(order.getCus_id(), order.getWal_source(), billAmount);
//	            final String username = "rllprojectkrithiks@gmail.com";
//	            final String password = "Rllproject@123";
//	                 
//
//	            Properties prop = new Properties();
//	            prop.put("mail.smtp.host", "smtp.gmail.com");
//	            prop.put("mail.smtp.port", "587");
//	            prop.put("mail.smtp.auth", "true");
//	            prop.put("mail.smtp.starttls.enable", "true"); //TLS
//	            
//	            Session session = Session.getInstance(prop,
//	                    new javax.mail.Authenticator() {
//	                        protected PasswordAuthentication getPasswordAuthentication() {
//	                            return new PasswordAuthentication(username, password);
//	                        }
//	                    });
//
//	            try {
//
//	                Message message = new MimeMessage(session);
//	                message.setFrom(new InternetAddress("rllprojectkrithiks@gmail.com"));
//	                message.setRecipients(
//	                        Message.RecipientType.TO,
//	                        InternetAddress.parse("vgrabi004@gmail.com,sshanmu7@gmail.com,puiangtraining@gmail.com")
//	                );
//	                message.setSubject("Hi Prasanna Sir! - Your Order will be delivered within 10 mins");
//	                //message.setText(order.getOrd_status());
//	                message.setText("Hello there! \n Customer ID : " + Integer.toString(order.getCus_id()) + "\nStatus:" + order.getOrd_status() + 
//	                		"\n Bill Amount : " + Double.toString(order.getOrd_billamount()) + 
//	                		"\n Order Quantity : " +Integer.toString(order.getOrd_quantity()));
//
//	                Transport.send(message);
//
//	                System.out.println("Done");
//
//	            } catch (MessagingException e) {
//	                e.printStackTrace();
//	            }
	            
//	             final String ACCOUNT_SID = "ACc95cf18f7d969f80e792c7c290f765b6"; 
//	           final String AUTH_TOKEN = "1b189af98f9d9d9ab8f5ed2a18668933"; 
//	         
//	        
//	                Twilio.init(ACCOUNT_SID, AUTH_TOKEN); 
//	                Message message = Message.creator( 
//	                        new com.twilio.type.PhoneNumber("+91"+ cust.getCus_phn_no()),  
//	                        "MG354c90a7c4c9e8cf6edd3995047df6e7", 
//	                        " Thankyou"+cust.getCus_name()+" for placing order in TechieEats Have a nice day \nYour order Details ! \n  Customer ID : " + Integer.toString(order.getCus_id()) + "\nStatus:" + order.getOrd_status() + 
//	                		"\n Bill Amount : " + Double.toString(order.getOrd_billamount()) + 
//	                		"\n Order Quantity : " +Integer.toString(order.getOrd_quantity()))      
//	                    .create(); 
//	         
//	                System.out.println(message.getSid()); 
//	       
//	        
//
//	            SimpleMailMessage msg = new SimpleMailMessage();
//		        msg.setTo(email);
//
//		        msg.setSubject(" Thankyou "+cust.getCus_name()+" for  Placing order in TechieEats! - We will confirm your order shortly");
//               //message.setText(order.getOrd_status());
//                msg.setText("Your order Details ! \n  Customer ID : " + Integer.toString(order.getCus_id()) + "\nStatus:" + order.getOrd_status() + 
//                		"\n Bill Amount : " + Double.toString(order.getOrd_billamount()) + 
//                		"\n Order Quantity : " +Integer.toString(order.getOrd_quantity()));
//		        javaMailSender.send(msg);
	            return "Order Placed Successfully...and Amount Debited";
	        }
	        return "Insufficient Funds...";
	    }
	   
	    public String acceptOrRejectOrder(int ordId,int venId,String status) {
			Orders orders = dao.searchByOrderId(ordId);
		
			int vid = orders.getVen_id();
			int cid = orders.getCus_id();
			String walType = orders.getWal_source();
			double billAmount = orders.getOrd_billamount();
			if (vid!=venId) {
				return "You are unauthorized vendor...";
			} 
			if (status.toUpperCase().equals("YES")) {
				//message(orders.getOrd_id(),"ACCEPTED");
				//acceptrejectmail(orders.getOrd_id(),"ACCEPTED");
				return dao.updateStatus(ordId,"ACCEPTED");								
			} else {
				dao.updateStatus(ordId, "DENIED");
				wdao.refundWallet(cid, walType, billAmount);
				//message(orders.getOrd_id(),"DENIED");
				//acceptrejectmail(orders.getOrd_id(),"DENIED");
				return "Order Rejected and Amount Refunded...";
			}
			
		}
	    
//	    public void message(int ordId,String sts) {
//	    
//			Orders order = dao.searchByOrderId(ordId);
//			String mail = cdao.getMailId(order.getCus_id());
//	        Menu menu = mdao.searchMenu(order.getMen_id());
//	        Customer cust=scus.get(order.getCus_id());
//	        Wallet wallet = wdao.showCustomerWallet(order.getCus_id(), order.getWal_source());
//	        
//	        final String ACCOUNT_SID = "ACc95cf18f7d969f80e792c7c290f765b6"; 
//	           final String AUTH_TOKEN = "1b189af98f9d9d9ab8f5ed2a18668933"; 
//	         
//	        
//	                Twilio.init(ACCOUNT_SID, AUTH_TOKEN); 
//	                Message message = Message.creator( 
//	                        new com.twilio.type.PhoneNumber("+91"+ cust.getCus_phn_no()),  
//	                        "MG354c90a7c4c9e8cf6edd3995047df6e7", 
//	                        " Hi"+cust.getCus_name()+" Your Order in TechieEats is " + sts +" For the below Order!\nCustomer ID:" + Integer.toString(order.getCus_id()) + "\nMenu Item: " + menu.getMen_item() +
//	                		"\nBill Amount : " + Double.toString(order.getOrd_billamount()) + "\nWallet Chosen: " + wallet.getWal_source() +
//	                		"\nStatus:" + sts)      
//	                    .create(); 
//	         
//	                System.out.println(message.getSid()); 
//	    }
//		
//		public void acceptrejectmail(int ordId,String sts) {
//			Orders order = dao.searchByOrderId(ordId);
//			String mail = cdao.getMailId(order.getCus_id());
//	        Menu menu = mdao.searchMenu(order.getMen_id());
//	        Customer cust=scus.get(order.getCus_id());
//	        Wallet wallet = wdao.showCustomerWallet(order.getCus_id(), order.getWal_source());
//			SimpleMailMessage msg = new SimpleMailMessage();
//	        msg.setTo(mail);
//	        msg.setSubject("Hi "+cust.getCus_name()+" Your Order in TechieEats is " + sts);
//           //message.setText(order.getOrd_status());
//            msg.setText("For the below Order! \nCustomer ID : " + Integer.toString(order.getCus_id()) + "\nMenu Item: " + menu.getMen_item() +
//            		"\nBill Amount : " + Double.toString(order.getOrd_billamount()) + "\nWallet Chosen: " + wallet.getWal_source() +
//            		"\nStatus:" + sts);
//            javaMailSender.send(msg);			
//		}

	    public List<Orders> showVendorPendingOrders(int venId) {
			return dao.showVendorPendingOrders(venId);
		}
		public List<Orders> showVendorOrders(int venId) {
			return dao.showVendorOrders(venId);
		}
		public List<Orders> showCustomerOrders(int custId) {
			return dao.showCustomerOrders(custId);
		}
		public List<Orders> showCustomerPendingOrders(int custId) {
			return dao.showCustomerPendingOrders(custId);
		}
		public List<Orders> showOrders() {
	        return repo.findAll();
	    }
	
}


