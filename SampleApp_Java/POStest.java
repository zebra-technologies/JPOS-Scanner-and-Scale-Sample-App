//------------------------------------------------------------------------------
//
// This software is provided "AS IS".  360Commerce MAKES NO
// REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE,
// EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NON-INFRINGEMENT. 360Commerce shall not be liable for
// any damages suffered as a result of using, modifying or distributing this
// software or its derivatives. Permission to use, copy, modify, and distribute
// the software and its documentation for any purpose is hereby granted.
//
// POStest.java - The main class for POStest
//
//------------------------------------------------------------------------------
// contribution of interface and implementation Rory K. Shaw/Raleigh/IBM 6-28-04
//------------------------------------------------------------------------------
// final framework completed 7-15-2004 JMenuBar, JMenuItem, ActionListeners
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.*;
import java.awt.event.*; 			// no windowClosing event
import java.net.*;
import java.io.*;

public class POStest extends JMenuBar {		// 6/21 whole gui is now JFrame
	public static final String version = "v1.9.1";          // 7/12
	
	private Container c;
	private JMenuBar menubar;
/*	private JMenuItem New, Open, Save, Exit, Undo, Cut, Copy, Paste;*/
	private JMenuItem Exit;
/*	private JMenuItem AboutJavaPOS, AboutJavaPOSTester, GoGetHelp;*/
	private JMenuItem item;
/*
	String[] fileItems = new String[] {"New", "Open", "Save", "Exit"};
	String[] editItems = new String[] {"Undo", "Cut", "Copy", "Paste"};
	private ServerSocket serverSock;
	private final int PORT = 1234;
	private final int MAX_IMAGE_SIZE = 75000;
*/
	
	public POStest(){
		
		menubar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
/*
 		JMenu editMenu = new JMenu("Edit");
		JMenu otherMenu = new JMenu("Other");
		JMenu helpMenu = new JMenu("Help");
*/
		menubar.add(fileMenu);
/*
 		menubar.add(editMenu);
		menubar.add(otherMenu);
		menubar.add(helpMenu);
*/
		menubar.setBorder(new BevelBorder(BevelBorder.RAISED));
/*		
		New = new JMenuItem("New");
		Open = new JMenuItem("Open");
		Save = new JMenuItem("Save");
*/
		Exit = new JMenuItem("Exit");
/*
		Undo = new JMenuItem("Undo");
		Cut = new JMenuItem("Cut");
		Copy = new JMenuItem("Copy");
		Paste = new JMenuItem("Paste");
		AboutJavaPOS = new JMenuItem("About JavaPOS");
		AboutJavaPOSTester = new JMenuItem("About JavaPOS Tester");
		GoGetHelp = new JMenuItem("Go To Retail Website");
		
		
		fileMenu.add(New);
		fileMenu.add(Open);
		fileMenu.add(Save);
*/
		fileMenu.add(Exit);
		 
/*		
		editMenu.add(Undo);
		editMenu.add(Cut);
		editMenu.add(Copy);
		editMenu.add(Paste);
		 
		otherMenu.add(item = new JMenuItem("IBM",
				             new ImageIcon("image.gif")));
		
		helpMenu.add(AboutJavaPOS);
		helpMenu.add(AboutJavaPOSTester);
		helpMenu.add(GoGetHelp);
*/		
		add(fileMenu);
/*
		add(editMenu);
		add(otherMenu);
		add(helpMenu);
*/		
		hookUpEvents();
	}
	
	private void hookUpEvents(){
/*		
		New.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println("New!!!!!!!!!!!");				
			}			
		});
		
		Open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println("Open!!!!!!!!!!!");				
			}			
		}); 
		
		Save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println("Save!!!!!!!!!!!");				
			}			
		});
*/		
		Exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
//				System.out.println("...Exit JavaPOS Tester...");
				System.exit(0);
			}			
		});
/*		
		Undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println("Undo!!!!!!!!!!!");				
			}			
		});
		
		Cut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println("Cut!!!!!!!!!!!");				
			}			
		});
		
		Copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println("Copy!!!!!!!!!!!");				
			}			
		});
		
		Paste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println("Paste!!!!!!!!!!!");				
			}			
		});
		
		AboutJavaPOS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println("About JavaPOS!!!!!!!!!!!");				
			}			
		}); 
		
		AboutJavaPOSTester.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println("About JavaPOS Tester!!!!!!!!!!!");				
			}			
		});
		
		GoGetHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					serverSock = new ServerSocket(PORT);					
				}catch (IOException ioe) {
					System.out.println("Unable to attach to port " + PORT);
				}
				System.out.println("About JavaPOS Tester!!!!!!!!!!!");
				Socket link = null;
				BufferedReader in;
				ObjectOutputStream oos = null;
				InputStream file = null;
				String address = new String("http://www.ibm.com/us/");
				URL webSite;
				ImageIcon icon = null;
				
				while(true){
					try {
						link = serverSock.accept();
						InputStream is = link.getInputStream();
						in = new BufferedReader(new InputStreamReader(is));
						OutputStream os = link.getOutputStream();
						oos = new ObjectOutputStream(os);					
					}catch (IOException ioe){
						ioe.printStackTrace();
						
					}
					
					try {
						webSite = new URL(address);
						icon = new ImageIcon(webSite);
						oos.writeObject(icon);						
						
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					JLabel content = new JLabel(icon);
					JFrame jf = new JFrame();
					jf.add(new JScrollPane(content), BorderLayout.CENTER);
					jf.setSize(400,400);
					jf.setLocation(50,50);
					jf.show();				
					
				}
				
			}			
		});
		*/
	}
	
	public static void main(String[] args) {
		    
		POStestGUI gui = new POStestGUI();
	                           
        JFrame frame = new JFrame();
     
        frame.setTitle("JavaPOStester in Progress");			// 6-21
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.setJMenuBar(new POStest());						// 7-15
        frame.getContentPane().add(gui,BorderLayout.CENTER);  
        frame.pack();											// 6-21
        frame.setSize(850,700 );
        frame.setVisible(true);

    }

}


    

