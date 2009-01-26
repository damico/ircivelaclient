package org.jdamico.ircivelaclient.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class UsersPanel extends JPanel implements ListSelectionListener, MouseListener{
	
	//private JTree usersPanel;
	private JList usersListPanel;
	private DefaultListModel listModel;
	private JPopupMenu popupMenu;
	 
    private Object selectedUser;
    private HandleApplet parent;
    
 
    
    Icon rootImg = new ImageIcon(this.getClass().getResource("/org/jdamico/ircivelaclient/view/images/icon-forum.gif"));
    Icon userImg = new ImageIcon(this.getClass().getResource("/org/jdamico/ircivelaclient/view/images/icon-online.gif"));
    
    
    
	public UsersPanel(HandleApplet handleApplet) {
		this.initializeList();
		this.initializePopMenu();
		this.setLayout(new BorderLayout());
		this.parent = handleApplet;
		//this.setSize(new Dimension(100,100));
		this.add(new JScrollPane(this.usersListPanel),BorderLayout.CENTER);
		 
		this.usersListPanel.addListSelectionListener(this);
		this.usersListPanel.addMouseListener(this);

	}

	public void initializePopMenu(){
		this.popupMenu = new JPopupMenu();
		PopupActionListener popupActionListener = new PopupActionListener();
		JMenuItem banItem = new JMenuItem("Ban");
		banItem.addActionListener(popupActionListener);
		JMenuItem kickItem = new JMenuItem("Kick");
		kickItem.addActionListener(popupActionListener);
		this.popupMenu.addSeparator();
		this.popupMenu.add(banItem);
		this.popupMenu.add(kickItem);
		
		
		
		
	}
	
	public void initializeList(){
		 this.listModel = new DefaultListModel();
		 this.usersListPanel = new JList(listModel);
		 this.usersListPanel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		 this.usersListPanel.setLayoutOrientation(JList.VERTICAL);

	}
	
	public void addUser(Object user){
		
		listModel.addElement(user.toString());
		 
	}
	
	public void removeUser(String user){
		 
		listModel.removeElement(user);
	}

	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount()==2 && selectedUser!=null){
		     
			if(selectedUser.toString().equalsIgnoreCase(StaticData.nick))
				JOptionPane.showMessageDialog(this, "You can not send private messages to yoursel. \n You're really a lonely person, aren't you?");
			else{
				if(selectedUser.toString().equalsIgnoreCase("channel"))
					return;
				parent.addTab(selectedUser.toString());
			}
				
		}else{
			if(e.getButton() == MouseEvent.BUTTON3){
				if(selectedUser!=null && !selectedUser.toString().equals("channel")){
					this.popupMenu.remove(0);
					this.popupMenu.add(new JLabel("  "+selectedUser.toString()), 0);
					
					this.popupMenu.show(this, e.getX(), e.getY());
				}else
					JOptionPane.showMessageDialog(this, "Please, select an user first to perform an action");
				 
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		 
		
	}

	//from list
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {

	        if (usersListPanel.getSelectedIndex() == -1) {
	        //No selection, disable fire button.
	            

	        } else {
	        //Selection, enable the fire button.
	           this.selectedUser = (String)usersListPanel.getSelectedValue();
	           System.out.println(this.selectedUser);
	        }
	    }

	}
}


//Define ActionListener
class PopupActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		 System.out.println("Selected: " + e.getActionCommand());
		
	}
  
}