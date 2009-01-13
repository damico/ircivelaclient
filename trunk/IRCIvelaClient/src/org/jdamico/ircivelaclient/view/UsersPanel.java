package org.jdamico.ircivelaclient.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.jdamico.ircivelaclient.config.Constants;

public class UsersPanel extends JPanel implements TreeSelectionListener, MouseListener{
	
	private JTree usersPanel;
	private JPopupMenu popupMenu;
	private DefaultMutableTreeNode root;
    private Object selectedUser;
    private HandleApplet parent;
    
 
    private int childIndex = 0;
    Icon rootImg = new ImageIcon(this.getClass().getResource("/org/jdamico/ircivelaclient/view/images/icon-forum.gif"));
    Icon userImg = new ImageIcon(this.getClass().getResource("/org/jdamico/ircivelaclient/view/images/icon-online.gif"));
    
    
    
	public UsersPanel(HandleApplet handleApplet) {
		this.initializeTree();
		this.initializePopMenu();
		this.setLayout(new BorderLayout());
		this.parent = handleApplet;
		//this.setSize(new Dimension(100,100));
		this.add(this.usersPanel,BorderLayout.CENTER);
		
		

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
	
	public void initializeTree(){
		Object[] hierarchy = new Object[]{Constants.PARAM_CHANNEL};
		this.root =  new DefaultMutableTreeNode(hierarchy[0]);
		
		this.usersPanel = new JTree(this.root);
		this.usersPanel.setScrollsOnExpand(true);
		this.usersPanel.addTreeSelectionListener(this);
		this.usersPanel.addMouseListener(this);
		
		DefaultTreeCellRenderer  rootRenderer = new DefaultTreeCellRenderer();
		rootRenderer.setOpenIcon(rootImg);
		rootRenderer.setClosedIcon(rootImg);
		rootRenderer.setLeafIcon(userImg);
		
		this.usersPanel.setCellRenderer(rootRenderer);
	}
	
	public void addUser(Object user){
		this.root.insert(new DefaultMutableTreeNode(user), childIndex++);
		//this.usersPanel.repaint();
		this.usersPanel.updateUI(); 
		
	}
	
	public void removeUser(String user){
		 
		int index = -1;
		Enumeration<Object> children = this.root.children();
		while(children.hasMoreElements()){
			index++;
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)children.nextElement();
			String userObject = (String)child.getUserObject();
			if(userObject.equalsIgnoreCase(user))
					break;
			 
				
		}
		if(index!=-1){
			this.root.remove(index);
			this.childIndex --;
			this.usersPanel.updateUI();
		}
			
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		// TODO Auto-generated method stub
		
			selectedUser = this.usersPanel.getLastSelectedPathComponent();
		 
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
}


//Define ActionListener
class PopupActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		 System.out.println("Selected: " + e.getActionCommand());
		
	}
  
}