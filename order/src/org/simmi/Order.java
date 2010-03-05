package org.simmi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SpinnerListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Order extends JApplet {
	JScrollPane	scrollpane = new JScrollPane();
	JScrollPane	pscrollpane = new JScrollPane();
	JScrollPane ascrollpane = new JScrollPane();
	JScrollPane rscrollpane = new JScrollPane();
	
	Set<String>	myVars = new HashSet<String>();
	
	JTable		table = new JTable();
	JTable		ptable = new JTable() {
		public Component prepareRenderer( TableCellRenderer renderer, int row, int column ) {
			Component ret = super.prepareRenderer(renderer, row, column);
			
			if( ptable.isCellEditable(row, column) ) ret.setEnabled( true );
			else ret.setEnabled( false );
			
			return ret;
		}
	};
	JTable		atable = new JTable();
	JTable		rtable = new JTable();
	
	Map<String,Integer>	modelRowMap = new HashMap<String,Integer>();
	
	JScrollPane	scrolled = new JScrollPane();
	JEditorPane	ed = new JEditorPane();
	
	JLabel		vorur = new JLabel( "Vörur", JLabel.CENTER );
	JLabel		pantanir = new JLabel( "Pantanir", JLabel.CENTER );
	JLabel		afgreitt = new JLabel( "Afgreitt", JLabel.CENTER );
	JLabel		afhent = new JLabel( "Afhent", JLabel.CENTER );
	
	JLabel		ylabel = new JLabel( "Nafn:", JLabel.CENTER );
	JLabel		label = new JLabel( "Framl:", JLabel.CENTER );
	JLabel		plabel = new JLabel( "Byrgir:", JLabel.CENTER );
	
	JComboBox	ycombo = new JComboBox();
	JComboBox	combo = new JComboBox();
	JComboBox	pcombo = new JComboBox();
	
	JComboBox	vcombo;
	JComboBox	stcombo;
	
	JButton		newItem = new JButton( "Ný vara" );
	JButton		delItem = new JButton( "Eyða vöru" );
	JButton		pantanytt = new JButton();
	
	JButton		afgreida = new JButton();
	JButton		afhenda = new JButton();
	
	JButton		excel = new JButton();
	JButton		vertbut = new JButton();
	
	TableModel	model;
	TableModel	pmodel;
	TableModel	amodel;
	TableModel	rmodel;
	
	TableModel	nullmodel;
	JComponent	c;
	JComponent	cb;
	JComponent	cvorur;
	JComponent	cpantanir;
	JComponent	cafgreitt;
	JComponent	cafhent;
	Connection	con;
	String		user;
	
	List<Pontun>	afglist = new ArrayList<Pontun>();
	List<Pontun>	afhlist = new ArrayList<Pontun>();
	List<Pontun>	pntlist = new ArrayList<Pontun>();
	List<Vara>		ordlist;
	
	int				ordno = 0;
	
	Pontun			currentSel = null;
	boolean			currentEdited = false;
	
	Map<String,Image>	faces = new HashMap<String,Image>();
	Map<String,String>	personMap = new HashMap<String,String>();
	
	VDialog 		d = new VDialog();
	Image			image = null;
	
	byte[] bb = new byte[1024];	
	Map<String,String>	pMap = new HashMap<String,String>();
	
	final Color bg = Color.white;//new Color( 0,0,0,0 );
	
	boolean	vert = true;
	
	public class VDialog extends JDialog {
		JLabel	name = new JLabel("Nafn:", JLabel.RIGHT );
		JLabel	cat = new JLabel("Cat:", JLabel.RIGHT );
		JLabel	frml = new JLabel("Framleiðandi:", JLabel.RIGHT );
		JLabel	brgr = new JLabel("Byrgir:", JLabel.RIGHT );
		
		JTextField	catField = new JTextField();
		JTextField	nameField = new JTextField();
		JComboBox	frmlCombo = new JComboBox();
		JComboBox	brgrCombo = new JComboBox();
		
		boolean		appr = false;
		
		JButton ok = new JButton( new AbstractAction("OK") {
			@Override
			public void actionPerformed(ActionEvent e) {
				appr = true;
				VDialog.this.setVisible( false );
			}
		});
		
		JButton cancel = new JButton( new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent e) {
				appr = false;
				VDialog.this.setVisible( false );
			}
		});
		
		public VDialog() {
			super();
			
			init();
		}
		
		public void setBounds( int x, int y, int w, int h ) {
			super.setBounds(x, y, w, h);
			
			name.setBounds(0, 15, 95, 25);
			cat.setBounds(275, 15, 95, 25);
			frml.setBounds(0, 45, 95, 25);
			brgr.setBounds(275, 45, 95, 25);
			
			nameField.setBounds(100, 15, 225, 25);
			catField.setBounds(375, 15, 150, 25);
			frmlCombo.setBounds(100, 45, 150, 25);
			brgrCombo.setBounds(375, 45, 150, 25);
			
			ok.setBounds(140, 100, 100, 25);
			cancel.setBounds(310, 100, 100, 25);
		}
		
		private void init() {
			this.setLayout( null );
			
			this.setUndecorated( true );
			this.setModal( true );
			this.setResizable( false );
			this.setTitle("Ný Vara");
			/*this.getRootPane().setBackground( Color.white );
			this.getRootPane().setForeground( Color.white );
			this.setBackground( Color.white );
			this.setForeground( Color.white );*/
			this.getContentPane().setBackground( Color.white );
			//this.getContentPane().setForeground( Color.white );
			
			this.add( name );
			this.add( cat );
			this.add( frml );
			this.add( brgr );
			
			this.add( nameField );
			this.add( catField );
			this.add( frmlCombo );
			this.add( brgrCombo );
			
			this.add(ok);
			this.add(cancel);
			
			frmlCombo.setEditable( true );
			brgrCombo.setEditable( true );
		}
	}
	
	public class Pontun {
		Boolean		_Mikilvægt;
		String		Byrgir;
		Integer		_Númer;
		String		Nafn;
		String		Pantað_Af;
		Integer		e_Magn;
		Date		Pantað;
		Date		_Afgreitt;
		Date		_Afhent;
		String		_Lýsing;
		String		e_Verknúmer;
		String		e_Svið;
		Integer		e_Verð;
		
		public Pontun( boolean urgent, String byrgir, int ordno, String name, String user, int quant, Date orddate, Date purdate, Date recdate, String description, String vn, String st, int price ) {
			this._Mikilvægt = urgent;
			this.Byrgir = byrgir;
			this._Númer = ordno;
			this.Nafn = name;
			this.Pantað_Af = user;
			this.e_Magn = quant;
			this.Pantað = orddate;
			this._Afgreitt = purdate;
			this._Afhent = recdate;
			this._Lýsing = description;
			if( vn == null ) { 
				this.e_Verknúmer = "";
			} else {
				this.e_Verknúmer = vn;
			}
			
			if( st == null ) {
				this.e_Svið = "";
			} else {
				this.e_Svið = st;
			}
			this.e_Verð = price;
		}
	}
	
	public class Vara {
		String  Nafn;
		String	Framleiðandi;
		String	Byrgir;
		Long	_Cat;
		
		public Vara( String name, String prdc, String selr, long cat ) {
			this.Nafn = name;
			this.Framleiðandi = prdc;
			this.Byrgir = selr;
			this._Cat = cat;
		}
	};
	
	static {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public TableModel createModel( final List<?> datalist ) {
		Class cls = null;
		if( cls == null && datalist.size() > 0 ) cls = datalist.get(0).getClass();
		return createModel( datalist, cls );
	}
	
	public TableModel createModel( final List<?> datalist, final Class cls ) {
		//System.err.println( cls );
		return new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return cls.getDeclaredFields()[columnIndex].getType();
			}

			@Override
			public int getColumnCount() {
				int cc = cls.getDeclaredFields().length-1;
				return cc;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return cls.getDeclaredFields()[columnIndex].getName().replace("e_", "").replace("_", " ");
			}

			@Override
			public int getRowCount() {
				return datalist.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Object ret = null;
				try {
					if( columnIndex >= 0 ) {
						Field f = cls.getDeclaredFields()[columnIndex];
						ret = f.get( datalist.get(rowIndex) );
						
						if( ret != null && ret.getClass() != f.getType() ) {
							System.err.println( ret.getClass() + "  " + f.getType() );
							ret = null;
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				return ret;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				Field[] ff = cls.getDeclaredFields();
				Field 	f = ff[columnIndex];
				//System.err.println( ff.length + "  " + columnIndex + "  " + f.getName() );
				return f.getName().startsWith("e_") && (this.getValueAt(rowIndex, 3).equals(user) || user.equals("ragnar") || user.equals("sigmar") );
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				Object o = datalist.get( rowIndex );
				Field f = cls.getDeclaredFields()[columnIndex];
				try {
					f.set( o, aValue );
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}
	
	public void loadPnt() throws SQLException {		
		String sql = "select [ordno], [name], [user], [quant], [orddate], [purdate], [urgent], [description], [jobid], [location], [recdate], [price], [byrgir] from [order].[dbo].[Pontun]";// where [user] = '"+user+"'";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		while (rs.next()) {
			Date 	afgdate = rs.getDate(6);
			Date	afhdate = rs.getDate(11);
			int		price = rs.getInt(12);
			String 	puser = rs.getString(3);
			String	pname = rs.getString(2);
			Pontun pnt = new Pontun( rs.getBoolean(7), rs.getString(13), rs.getInt(1), pname, puser, rs.getInt(4), rs.getDate(5), afgdate, afhdate, rs.getString(8), rs.getString(9), rs.getString(10), price );
			if( afgdate == null && afhdate == null ) pntlist.add( pnt );
			else if( afhdate == null ) afglist.add( pnt ); 
			else afhlist.add( pnt );
			
			if( user.equals(puser) ) {
				//System.err.println( "adding "+pname );
				myVars.add( pname );
			}
		}
		
		rs.close();
		ps.close();
		
		sql = "select max([ordno]) from [order].[dbo].[Pontun]";
		
		ps = con.prepareStatement(sql);
		rs = ps.executeQuery();

		while (rs.next()) {
			ordno = rs.getInt( 1 );
		}
		
		rs.close();
		ps.close();
	}
	
	public List<Vara> loadOrders() throws IOException, SQLException {
		InputStream			is = this.getClass().getResourceAsStream("/orders.txt");
		InputStreamReader 	ir = new InputStreamReader( is, "UTF-8" );
		BufferedReader		br = new BufferedReader( ir );
		
		List<Vara>	ordlist = new ArrayList<Vara>();
		
		String sql = "select [cat], [name], [byrgir], [framl] from [order].[dbo].[Vara]"; // where [user] = '"+user+"'";
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		while (rs.next()) {
			ordlist.add( new Vara( rs.getString(2), rs.getString(4), rs.getString(3), rs.getInt(1) ) );
		}
		
		rs.close();
		ps.close();
		
		String line = br.readLine();
		while( line != null ) {
			String[] spl = line.split("[\t]");
			int i = -1;
			try {
				i = Integer.parseInt(spl[4]);
			} catch( Exception e ) {
				
			}
			ordlist.add( new Vara( spl[0], spl[3], spl[6], i ) );
			
			line = br.readLine();
		}
		br.close();
		ir.close();
		
		return ordlist;
	}
	
	public String getUser() {
		return user;
	}
	
	/*public int getOrdno() {
		int l_ordno = ordno;
		
		if( pntlist != null ) {
			for( Pnt p : pntlist ) {
				if( p.ordno > l_ordno ) l_ordno = p.ordno;
			}
			l_ordno++;
		}
		
		return l_ordno;
	}*/
	
	public void updateorder( Pontun order ) throws SQLException {
		//String ord = ordno+",'"+name+"','"+user+"',"+quant+",GetDate(),null,0,null";
		String sql = "update [order].[dbo].[Pontun] set [quant] = "+order.e_Magn+", [Description] = '"+order._Lýsing+"', [Location] = '"+order.e_Svið+"', [jobid] = '"+order.e_Verknúmer+"', [price] = "+order.e_Verð+" where [ordno] = "+order._Númer;
		
		if( order._Afhent != null ) {
			sql = "update [order].[dbo].[Pontun] set [recdate] = GetDate(), [quant] = "+order.e_Magn+", [Description] = '"+order._Lýsing+"', [Location] = '"+order.e_Svið+"', [jobid] = '"+order.e_Verknúmer+"', [price] = "+order.e_Verð+" where [ordno] = "+order._Númer;
		} else if( order._Afgreitt != null ) {
			sql = "update [order].[dbo].[Pontun] set [purdate] = GetDate(), [quant] = "+order.e_Magn+", [Description] = '"+order._Lýsing+"', [Location] = '"+order.e_Svið+"', [jobid] = '"+order.e_Verknúmer+"', [price] = "+order.e_Verð+" where [ordno] = "+order._Númer;
		}
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		boolean				b = ps.execute();
		
		ps.close();
	}
	
	public void updateorder( Pontun order, int quant ) throws SQLException {
		//String ord = ordno+",'"+name+"','"+user+"',"+quant+",GetDate(),null,0,null";
		String sql = "update [order].[dbo].[Pontun] set [quant] = "+quant+" where [ordno] = "+order._Númer;
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		boolean				b = ps.execute();
		
		order.e_Magn = quant;
		//pntlist.get(index)
		/*if( !b ) {
			pntlist.add( new Pnt( false, ordno, name, user, quant, new Date( System.currentTimeMillis() ), null, "" ) );
		}*/
		
		ps.close();
	}
	
	public void order( String name, String byrgir, int quant, String verknr, String location ) throws SQLException {
		ordno++;
		String ord = ordno+",'"+name+"','"+user+"',"+quant+",GetDate(),null,0,null,'"+verknr.substring(0, 10)+"','"+location+"',null,0,'"+byrgir+"'";
		String sql = "insert into [order].[dbo].[Pontun] values ("+ord+")";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		boolean				b = ps.execute();
		
		if( !b ) {
			pntlist.add( new Pontun( false, byrgir, ordno, name, user, quant, new Date( System.currentTimeMillis() ), null, null, "", verknr, location, 0 ) );
		}
		
		ps.close();
	}
	
	public boolean disorder( int ordno ) throws SQLException {
		String sql = "delete from [order].[dbo].[Pontun] where ordno = "+ordno;
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		boolean				b = ps.execute();
		
		ps.close();
		
		return b;
	}
	
	public String loadEmp() throws IOException {
		URL url = new URL( "http://www.matis.is/um-matis-ohf/starfsfolk/svid/" );
		InputStream stream = url.openStream();
		
		int r = stream.read(bb);
		String ret = "";
		while( r > 0 ) {
			ret += new String( bb, 0, r, "UTF-8" );
			r = stream.read(bb);
		}
		
		return ret;
	}
	
	public void removeSelectedRows() {
		int[] 			rr = ptable.getSelectedRows();
		Set<Pontun>		remset = new HashSet<Pontun>();
		for( int r : rr ) {
			int 	rval = ptable.convertRowIndexToModel( r );
			if( rval != -1 ) {
				Object		ordobj = pmodel.getValueAt(rval, 2);
				if( ordobj != null ) {
					int			ordno = (Integer)ordobj;
					String 		username = (String)ptable.getValueAt(r, 2);
					
					if( username.equals(user) ) {
						try {
							if( !disorder( ordno ) ) {
								for( Pontun p : pntlist ) {
									if( p._Númer == ordno ) {
										remset.add( p );
									}
								}
							}
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}	
		pntlist.removeAll( remset );
		ptable.tableChanged( new TableModelEvent(pmodel) );
		//ptable.setModel( nullmodel );
		//ptable.setModel( pmodel );
	}
	
	public void updateVerk() throws SQLException {
		String sql = "select [No_], [Description] from [MATIS].[dbo].[Matís ohf_$Job] where [Blocked] = 0"; // where [user] = '"+user+"'";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		while (rs.next()) {
			vcombo.addItem( rs.getString(1) + " - " + rs.getString(2) );
		}
		
		rs.close();
		ps.close();
	}
	
	private void panta(String name, String byrgir) {
		int 		quant = 1;
		Pontun 		tpnt = null;
		for( Pontun pnt : pntlist ) {
			if( pnt.Nafn.equals( name ) && pnt.Pantað_Af.equals(user) ) {
				tpnt = pnt;
				break;
			}
		}
		
		try {
			if( tpnt != null ) {
				updateorder( tpnt, tpnt.e_Magn+1 );
			} else {
				order( name, byrgir, quant, (String)vcombo.getSelectedItem(), (String)stcombo.getSelectedItem() );
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void afh() {
		Set<Pontun>	pnts = new HashSet<Pontun>();
		int[] rr = atable.getSelectedRows();
		for( int r : rr ) {
			if( r != -1 ) {
				r = atable.convertRowIndexToModel( r );
				
				Pontun pnt = afglist.get(r);
				
				if( pnt.Pantað_Af.equals(user) || user.equals("ragnar") || user.equals("sigmar") ) {
					pnt._Afhent = new Date( System.currentTimeMillis() );
					pnts.add( pnt );
					
					try {
						updateorder( pnt );
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		
		afglist.removeAll( pnts );
		afhlist.addAll( pnts );
		
		atable.tableChanged( new TableModelEvent( amodel ) );
		rtable.tableChanged( new TableModelEvent( rmodel ) );
	}
	
	private void afgr() {
		Set<Pontun>	pnts = new HashSet<Pontun>();
		int[] rr = ptable.getSelectedRows();
		for( int r : rr ) {
			if( r != -1 ) {
				r = ptable.convertRowIndexToModel( r );
				
				Pontun pnt = pntlist.get(r);
				pnt._Afgreitt = new Date( System.currentTimeMillis() );
				pnts.add( pnt );
				
				try {
					updateorder( pnt );
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		pntlist.removeAll( pnts );
		afglist.addAll( pnts );
		
		ptable.tableChanged( new TableModelEvent( pmodel ) );
		atable.tableChanged( new TableModelEvent( amodel ) );
	}
	
	public void loadPersons() throws IOException {
		InputStream is = this.getClass().getResourceAsStream("/persons.txt");
		InputStreamReader	isr = new InputStreamReader( is, "UTF-8" );
		BufferedReader		br = new BufferedReader( isr );
		
		String line = br.readLine();
		while( line != null ) {
			String[]	s = line.split("\t");
			personMap.put( s[0], s[1] );
			
			line = br.readLine();
		}
	}
	
	public void panta() {
		int[] rr = table.getSelectedRows();
		for( int r : rr ) { 
			String		name = (String)table.getValueAt(r, 0);
			String		byrgir = (String)table.getValueAt(r, 2);
			//String		name = (String)table.getValueAt(r, 0);					
			panta(name,byrgir);
		}
		ptable.tableChanged( new TableModelEvent( pmodel ) );
	}
	
	public void delVar() {
		int[] rra = table.getSelectedRows();
		for( int r : rra ) {
			int rr = table.convertRowIndexToModel(r);
			int cat = (Integer)model.getValueAt(rr, 3);
			try {
				delItem( cat );
				for( Vara v : ordlist ) {
					if( v._Cat == cat ) {
						combo.removeItem( v.Framleiðandi );
						pcombo.removeItem( v.Byrgir );
						
						ordlist.remove( v );
						table.tableChanged( new TableModelEvent(model) );
						break;
					}
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public void init() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		Window window = SwingUtilities.windowForComponent(this);
		if (window instanceof JFrame) {
			JFrame frame = (JFrame)window;
			if (!frame.isResizable()) frame.setResizable(true);
		}
		
		try {
			loadPersons();
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		
		vcombo = new JComboBox() {
			private boolean layingOut = false;

			public void doLayout() {
		        try {
		            layingOut = true;
		            super.doLayout();
		        }
		        finally {
		            layingOut = false;
		        }
		    }

		    public Dimension getSize() {
		        Dimension sz = super.getSize();
		        if (!layingOut) {
		            sz.width = Math.max(sz.width, this.getPreferredSize().width);
		        }
		        return sz;
		    }
		};
		
		stcombo = new JComboBox() {
			private boolean layingOut = false;

			public void doLayout() {
		        try {
		            layingOut = true;
		            super.doLayout();
		        }
		        finally {
		            layingOut = false;
		        }
		    }

		    public Dimension getSize() {
		        Dimension sz = super.getSize();
		        if (!layingOut) {
		            sz.width = Math.max(sz.width, this.getPreferredSize().width);
		        }
		        return sz;
		    }
		};
		
		try {
			String val = loadEmp();
			String[] vals = val.split( "<h3>" );
			
			for( int i = 1; i < vals.length; i++ ) {
				String sstr = vals[i];
				int hind = sstr.indexOf("</h3>");
				String svid = sstr.substring(0, hind);
				
				String[] subs = sstr.split("<a href=\"/um-matis-ohf/");
				if( subs.length > 1 ) {
					String currentStr = null;
					for( String str : subs ) {
						currentStr = str;
						int ind = str.indexOf("</a>");
						if( ind > 0 ) {
							String person = str.substring(0,ind);
							if( !person.contains("fyrirtaeki") ) {
								break;
							} else {
								pMap.put(person, svid);
							}
						}
					}
					if( !subs[ subs.length-1 ].equals(currentStr) ) break;
				}
			}
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=MATIS;user=simmi;password=drsmorc.311;";
			con = DriverManager.getConnection(connectionUrl);
			
			updateVerk();
			
			con.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		stcombo.addItem("Erfðir og eldi");
		stcombo.addItem("Mælingar og miðlun");
		
		boolean valid = false;
		try {
			String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;integratedSecurity=true;";
			//String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;user=simmi;password=drsmorc.311;";
			con = DriverManager.getConnection(connectionUrl);
			valid = con.isValid(2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if( !valid ) {
			try {
				String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;user=simmi;password=drsmorc.311;";
				con = DriverManager.getConnection(connectionUrl);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		nullmodel = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public Class<?> getColumnClass(int columnIndex) {return null;}

			@Override
			public int getColumnCount() {return 0;}

			@Override
			public String getColumnName(int columnIndex) {return null;}

			@Override
			public int getRowCount() {return 0;}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {return null;}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {return false;}

			@Override
			public void removeTableModelListener(TableModelListener l) {}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
		};
		
		pantanytt.setAction( new AbstractAction("Panta nýja vöru") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Vara v = newItem();
					if( v != null ) {
						panta( v.Nafn, v.Byrgir );
						ptable.tableChanged( new TableModelEvent( pmodel ) );
						table.tableChanged( new TableModelEvent( model ) );
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		vertbut.setAction( new AbstractAction("Raða töflum") {
			@Override
			public void actionPerformed(ActionEvent e) {
				vert = !vert;
				c.revalidate();
				c.repaint();
			}
		});
		
		excel.setAction( new AbstractAction("Skoða í Excel") {
			@Override
			public void actionPerformed(ActionEvent e) {
				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet1 = workbook.createSheet("Vörur");
				XSSFSheet sheet2 = workbook.createSheet("Pantanir");
				XSSFSheet sheet3 = workbook.createSheet("Afgreitt");
				XSSFSheet sheet4 = workbook.createSheet("Afhent");
				
				XSSFSheet[] sheets = { sheet1, sheet2, sheet3, sheet4 };
				JTable[] 	tables = { table, ptable, atable, rtable };
				
				fillExcel( sheets, tables );
				
				File xlsxFile;
				try {
					xlsxFile = File.createTempFile("tmp", ".xlsx");
					workbook.write( new FileOutputStream( xlsxFile ) );
					Desktop.getDesktop().open( xlsxFile );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			private void fillExcel(XSSFSheet[] sheets, JTable[] tables) {
				for( int i = 0; i < tables.length; i++ ) {
					XSSFSheet 	sheet = sheets[i];
					JTable		table = tables[i];
					
					XSSFRow row = sheet.createRow(0);
					for( int c = 0; c < table.getColumnCount(); c++ ) {
						String cname = table.getColumnName(c);
						XSSFCell cell = row.createCell(c);
						cell.setCellValue(cname);
					}
					
					for( int r = 0; r < table.getRowCount(); r++ ) {
						row = sheet.createRow(r+1);
						for( int c = 0; c < table.getColumnCount(); c++ ) {
							XSSFCell cell = row.createCell(c);
							Object obj = table.getValueAt( r, c );
							if( obj instanceof String ) cell.setCellValue( (String)obj );
							else if( obj instanceof Integer ) cell.setCellValue( (Integer)obj );
							else if( obj instanceof Date ) cell.setCellValue( ((Date)obj).toString() );
							else if( obj instanceof Boolean ) cell.setCellValue( (Boolean)obj );
						}
					}
				}
			}		
		});
			
		final JButton	addbtn = new JButton( new AbstractAction("Panta >>") {
			public void actionPerformed(ActionEvent e) {
				panta();
				//ptable.setModel( nullmodel );
				//ptable.setModel( pmodel );
			}
		});
		final JButton	rembtn = new JButton(new AbstractAction("<< Afpanta") {
			public void actionPerformed(ActionEvent e) {
				removeSelectedRows();
			}
		});
		
		afgreida.setAction( new AbstractAction("<< Afgreiða") {
			@Override
			public void actionPerformed(ActionEvent e) {
				afgr();
			}
		});
		
		afhenda.setAction( new AbstractAction("Afhenda >>") {
			@Override
			public void actionPerformed(ActionEvent e) {
				afh();
			}
		});
		
		final String domain = System.getenv("USERDOMAIN");
		/*Map<String,String>	env = System.getenv();
		for( String e : env.keySet() ) {
			System.err.println( e );
		}*/
		//System.err.println( domain );
		user = System.getProperty("user.name");
		
		try {
			ordlist = loadOrders();
			model = createModel( ordlist );
			
			loadPnt();
			pmodel = createModel( pntlist, Pontun.class );
			amodel = createModel( afglist, Pontun.class );
			rmodel = createModel( afhlist, Pontun.class );
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cb = new JComponent() {
			public void setBounds( int x, int y, int w, int h ) {
				super.setBounds(x, y, w, h);
				
				if( vert ) {
					afhenda.setBounds( (int)(0.9*w)-75, (int)(0.5*h), 150, 25 );
					
					if( cb.isAncestorOf( cafgreitt ) ) cafgreitt.setBounds( (int)(0.00*w), (int)(0.0*h), (int)(0.80*w), (int)(0.5*h)-5 );
					if( cb.isAncestorOf( cafhent ) ) cafhent.setBounds( (int)(0.00*w), (int)(0.5*h)+5, (int)(0.80*w), (int)(0.5*h) );
					
					//vertbut.setBounds( (int)(0.9*w)-75, this.getHeight()-80, 150, 25 );
					//excel.setBounds( (int)(0.9*w)-75, this.getHeight()-50, 150, 25 );
				} else {
					//vcombo.setBounds( (int)(0.5*w)-75, 260, 150, 25 );
					//stcombo.setBounds( (int)(0.5*w)-75, 290, 150, 25 );
					
					//addbtn.setBounds( (int)(0.5*w)-75, 340, 150, 25 );
					//rembtn.setBounds( (int)(0.5*w)-75, 370, 150, 25 );
					//pantanytt.setBounds( (int)(0.5*w)-75, 400, 150, 25 );
					
					//afgreida.setBounds( (int)(0.5*w)-75, (int)(0.6*h)+100, 150, 25 );
					afhenda.setBounds( (int)(0.5*w)-75, (int)(0.5*h), 150, 25 );
					
					//cvorur.setBounds( (int)(0.00*w), 30, (int)(0.40*w), (int)(0.50*h) );
					//cpantanir.setBounds( (int)(0.60*w), 30, (int)(0.40*w), (int)(0.50*h) );
					if( cb.isAncestorOf( cafgreitt ) ) cafgreitt.setBounds( (int)(0.00*w), (int)(0.0*h), (int)(0.40*w), (int)(1.0*h) );
					if( cb.isAncestorOf( cafhent ) ) cafhent.setBounds( (int)(0.60*w), (int)(0.0*h), (int)(0.40*w), (int)(1.0*h) );
					
					//vertbut.setBounds( (int)(0.5*w)-75, this.getHeight()-80, 150, 25 );
					//excel.setBounds( (int)(0.5*w)-75, this.getHeight()-50, 150, 25 );
				}
			}
		};
		cb.setLayout( null );
		
		c = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent(g);
				
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
				
				Font f = g.getFont();
				g.setFont( f.deriveFont( f.getSize()+5.0f ) );
				
				String str = "Velkomin(n) "+user;
				if( domain != null ) str += " on "+domain;
				int strw = g.getFontMetrics().stringWidth( str );
				if( !vert ) {
					g.drawString( str, (this.getWidth()-strw)/2, 20 );
					
					if( user != null ) {
						Image face = getImage(user);
						if( face != null ) g.drawImage( face, (this.getWidth()-face.getWidth(this))/2, 50, this );
					}
				} else {
					g.drawString( str, (int)(this.getWidth()*0.9-strw/2), 20 );
					
					if( user != null ) {
						Image face = getImage(user);
						if( face != null ) g.drawImage( face, (int)(this.getWidth()*0.9-face.getWidth(this)/2), 50, this );
					}
				}
			}
			
			public void setBounds( int x, int y, int w, int h ) {
				super.setBounds(x, y, w, h);
				
				if( vert ) {
					vcombo.setBounds( (int)(0.9*w)-75, 260, 150, 25 );
					stcombo.setBounds( (int)(0.9*w)-75, 290, 150, 25 );
					
					addbtn.setBounds( (int)(0.9*w)-75, 340, 150, 25 );
					rembtn.setBounds( (int)(0.9*w)-75, 370, 150, 25 );
					pantanytt.setBounds( (int)(0.9*w)-75, 400, 150, 25 );
					
					afgreida.setBounds( (int)(0.9*w)-75, (int)(0.6*h)+100, 150, 25 );
					//afhenda.setBounds( (int)(0.9*w)-75, (int)(0.6*h)+130, 150, 25 );
					
					if( c.isAncestorOf( cvorur ) ) cvorur.setBounds( (int)(0.00*w), 0, (int)(0.80*w), (int)(0.50*h) );
					if( c.isAncestorOf( cpantanir ) ) cpantanir.setBounds( (int)(0.00*w), (int)(0.50*h), (int)(0.80*w), (int)(0.5*h) );
					//cafgreitt.setBounds( (int)(0.00*w), (int)(0.56*h), (int)(0.80*w), (int)(0.22*h) );
					//cafhent.setBounds( (int)(0.00*w), (int)(0.78*h), (int)(0.80*w), (int)(0.22*h) );
					
					vertbut.setBounds( (int)(0.9*w)-75, this.getHeight()-80, 150, 25 );
					excel.setBounds( (int)(0.9*w)-75, this.getHeight()-50, 150, 25 );
				} else {
					vcombo.setBounds( (int)(0.5*w)-75, 260, 150, 25 );
					stcombo.setBounds( (int)(0.5*w)-75, 290, 150, 25 );
					
					addbtn.setBounds( (int)(0.5*w)-75, 340, 150, 25 );
					rembtn.setBounds( (int)(0.5*w)-75, 370, 150, 25 );
					pantanytt.setBounds( (int)(0.5*w)-75, 400, 150, 25 );
					
					afgreida.setBounds( (int)(0.5*w)-75, (int)(0.6*h)+100, 150, 25 );
					//afhenda.setBounds( (int)(0.5*w)-75, (int)(0.5*h), 150, 25 );
					
					if( c.isAncestorOf( cvorur ) ) cvorur.setBounds( (int)(0.00*w), 30, (int)(0.40*w), (int)(1.0*h) );
					if( c.isAncestorOf( cpantanir ) ) cpantanir.setBounds( (int)(0.60*w), 30, (int)(0.40*w), (int)(0.95*h) );
					//cafgreitt.setBounds( (int)(0.00*w), (int)(0.55*h), (int)(0.40*w), (int)(0.45*h) );
					//cafhent.setBounds( (int)(0.60*w), (int)(0.55*h), (int)(0.40*w), (int)(0.45*h) );
					
					vertbut.setBounds( (int)(0.5*w)-75, this.getHeight()-80, 150, 25 );
					excel.setBounds( (int)(0.5*w)-75, this.getHeight()-50, 150, 25 );
				}
			}
		};

		this.setBackground( bg );
		this.getContentPane().setBackground( bg );
		
		ycombo.addItem("Allir");
		combo.addItem("Allir");
		pcombo.addItem("Allir");
		
		Set<String>	comboOptions = new TreeSet<String>();
		Set<String>	pcomboOptions = new TreeSet<String>();
		
		for( int r = 0; r < model.getRowCount(); r++ ) {
			String str = (String)model.getValueAt(r, 1);
			if( str != null && str.length() > 0 ) {
				comboOptions.add(str);
			}
		}
		
		for( int r = 0; r < model.getRowCount(); r++ ) {
			String str = (String)model.getValueAt(r, 2);
			if( str != null && str.length() > 0 ) {
				pcomboOptions.add(str);
			}
		}
		
		ycombo.addItem("Mínar vörur");
		
		for( String str : comboOptions ) {
			d.frmlCombo.addItem( str );
			combo.addItem(str);
		}
		
		for( String str : pcomboOptions ) {
			d.brgrCombo.addItem( str );
			pcombo.addItem(str);
		}
		
		ycombo.addItemListener( new ItemListener() {	
			@Override
			public void itemStateChanged(ItemEvent e) {
				combo.setSelectedItem("Allir");
				pcombo.setSelectedItem("Allir");
				((TableRowSorter<TableModel>)table.getRowSorter()).setRowFilter( new RowFilter<TableModel, Integer>() {
					@Override
					public boolean include(javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
						String sel = (String)ycombo.getSelectedItem();
						if( sel.equals("Allir") ) return true; 
						int r = entry.getIdentifier();
						Object str = (String)model.getValueAt( r, 0 );
						if( myVars.contains(str) ) return true;
						return false;
					}
				});
			}
		});
		
		combo.addItemListener( new ItemListener() {	
			@Override
			public void itemStateChanged(ItemEvent e) {
				ycombo.setSelectedItem("Allir");
				pcombo.setSelectedItem("Allir");
				((TableRowSorter<TableModel>)table.getRowSorter()).setRowFilter( new RowFilter<TableModel, Integer>() {
					@Override
					public boolean include(javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
						String sel = (String)combo.getSelectedItem();
						if( sel.equals("Allir") ) return true; 
						int r = entry.getIdentifier();
						Object str = (String)model.getValueAt( r, 1 );
						if( str.equals( sel ) ) return true;
						return false;
					}
				});
			}
		});
		
		pcombo.addItemListener( new ItemListener() {	
			@Override
			public void itemStateChanged(ItemEvent e) {
				ycombo.setSelectedItem("Allir");
				combo.setSelectedItem("Allir");
				((TableRowSorter<TableModel>)table.getRowSorter()).setRowFilter( new RowFilter<TableModel, Integer>() {
					@Override
					public boolean include(javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
						String sel = (String)pcombo.getSelectedItem();
						if( sel.equals("Allir") ) return true; 
						int r = entry.getIdentifier();
						Object str = (String)model.getValueAt( r, 2 );
						if( str.equals( sel ) ) return true;
						
						return false;
					}
				});
			}
		});
		
		JPopupMenu	popup = new JPopupMenu();
		popup.add( new AbstractAction("Panta") {
			@Override
			public void actionPerformed(ActionEvent e) {
				panta();
			}
		});
		popup.add( new AbstractAction("Eyða vöru(m)") {
			@Override
			public void actionPerformed(ActionEvent e) {
				delVar();
			}
		});
		table.setComponentPopupMenu( popup );
		
		popup = new JPopupMenu();
		popup.add( new AbstractAction("Afgreiða") {
			@Override
			public void actionPerformed(ActionEvent e) {
				afgr();
			}
		});
		ptable.setComponentPopupMenu( popup );
		
		popup = new JPopupMenu();
		popup.add( new AbstractAction("Afhenda") {
			@Override
			public void actionPerformed(ActionEvent e) {
				afh();
			}
		});
		atable.setComponentPopupMenu( popup );
		
		
		//table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		//table.setColumnSelectionAllowed( true );
		table.setAutoCreateRowSorter( true );
		table.setModel( model );
		ptable.setAutoCreateRowSorter( true );
		ptable.setModel( pmodel );
		atable.setAutoCreateRowSorter( true );
		atable.setModel( amodel );
		rtable.setAutoCreateRowSorter( true );
		rtable.setModel( rmodel );
		
		if( myVars.size() > 0 ) {
			ycombo.setSelectedItem("Mínar vörur");
		}
		
		for( int r = 0; r < model.getRowCount(); r++ ) {
			modelRowMap.put( (String)model.getValueAt(r, 0), r );
		}
		
		rtable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if( currentEdited ) {
					try {
						updateorder(currentSel);
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					currentEdited = false;
				}
				
				int r = rtable.getSelectedRow();
				if( r != -1 ) {					
					int mr = rtable.convertRowIndexToModel(r);
					Pontun pnt = afhlist.get(mr);
					currentSel = pnt;
				}
			}
		});
		
		ptable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			//String oldname = "";
			
			public void valueChanged(ListSelectionEvent e) {
				int r = ptable.getSelectedRow();
				if( currentSel != null && !currentSel.equals(ed.getText()) ) {
					if( !ed.getText().equals(currentSel._Lýsing) ) currentEdited = true;
					
					if( currentEdited ) {
						currentSel._Lýsing = ed.getText().replaceAll("'", "");
						try {
							updateorder(currentSel);
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
						
						currentEdited = false;
					}
				}
				
				if( r != -1 ) {					
					int mr = ptable.convertRowIndexToModel(r);
					Pontun pnt = pntlist.get(mr);
					currentSel = pnt;
					ed.setText( currentSel._Lýsing );
					
					if( pnt.Pantað_Af.equals(user) ) {
						ed.setEditable( true );
					} else {
						ed.setEditable( false );
					}
					
					String name = (String)ptable.getValueAt(r, 2);
					String vara = (String)ptable.getValueAt(r, 1);
					
					if( vara != null && modelRowMap.containsKey( vara ) ) {
						int nr = modelRowMap.get( vara );
						if( nr != -1 ) {
							nr = table.convertRowIndexToView( nr );
							if( nr >= 0 && nr <= table.getRowCount() ) {
								table.setRowSelectionInterval( nr, nr );
								table.scrollRectToVisible( table.getCellRect(nr, 0, false) );
							} else {
								System.err.println( nr );
							}
						}
					}
					
					image = getImage( name );
					cpantanir.repaint();
					
					/*if( !name.equals( oldname ) ) {
						ll
						oldname = name;
					}*/
				}
			}
		});
		
		//table.getColumnModel().getColumn(0).
		//ptable.getColumn("Magn").setCellEditor( new SpinnerEditor(items));
		//ptable.getColumn("Magn").setCellRenderer( TableColumn)
		
		ptable.addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_DELETE ) {
					removeSelectedRows();
				}
			}
		});
		
		/*ptable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int r = ptable.getSelectedRow();
				if( r != -1 ) {
					String pantAri = (String)ptable.getValueAt( r, 2 );
					image = getImage( pantAri );
					cpantanir.repaint();
				}
			}
		});*/
		
		atable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if( currentEdited ) {
					try {
						updateorder(currentSel);
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					currentEdited = false;
				}
				
				int r = atable.getSelectedRow();
				if( r != -1 ) {
					int mr = rtable.convertRowIndexToModel(r);
					Pontun pnt = afglist.get(mr);
					currentSel = pnt;
					
					String name = (String)atable.getValueAt(r, 2);
					if( user.equals(name) || user.equals("ragnar") || user.equals("sigmar") ) {
						afhenda.setEnabled( true );
					} else {
						afhenda.setEnabled( false );
					}
				}
			}
		});
		
		Field[] decl = Pontun.class.getDeclaredFields();
		Field[] odecl = Vara.class.getDeclaredFields();
		
		Set<TableColumn>			remcol = new HashSet<TableColumn>();
		Enumeration<TableColumn>	taben = table.getColumnModel().getColumns();
		while( taben.hasMoreElements() ) {
			TableColumn tc = taben.nextElement();
			String name = odecl[tc.getModelIndex()].getName();
			System.err.println( name );
			if( name.startsWith("_") ) {
				remcol.add( tc );
			}
		}
		
		for( TableColumn tc : remcol ) {
			table.removeColumn( tc );
		}
		
		remcol.clear();
		taben = ptable.getColumnModel().getColumns();
		while( taben.hasMoreElements() ) {
			TableColumn tc = taben.nextElement();
			String name = decl[tc.getModelIndex()].getName();
			if( name.startsWith("_") || name.equals("e_Verð") ) {
				remcol.add( tc );
			}
		}
		
		for( TableColumn tc : remcol ) {
			ptable.removeColumn( tc );
		}
		
		remcol.clear();
		taben = atable.getColumnModel().getColumns();
		while( taben.hasMoreElements() ) {
			TableColumn tc = taben.nextElement();
			String name = decl[tc.getModelIndex()].getName();
			if( (name.startsWith("_") || name.equals("e_Mikilvægt")) && !name.equals("_Afgreitt") ) {
				remcol.add( tc );
			}
		}
		
		for( TableColumn tc : remcol ) {
			atable.removeColumn( tc );
		}
		
		remcol.clear();
		taben = rtable.getColumnModel().getColumns();
		while( taben.hasMoreElements() ) {
			TableColumn tc = taben.nextElement();
			String name = decl[tc.getModelIndex()].getName();
			if( (name.startsWith("_") || name.equals("e_Mikilvægt")) && !name.equals("_Afgreitt") ) {
				remcol.add( tc );
			}
		}
		
		for( TableColumn tc : remcol ) {
			rtable.removeColumn( tc );
		}
		
		newItem.setAction( new AbstractAction("Ný vara") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					newItem();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		delItem.setAction( new AbstractAction("Eyða vöru") {
			@Override
			public void actionPerformed(ActionEvent e) {
				delVar();
			}
		});
		
		scrolled.setViewportView( ed );
		
		scrollpane.setViewportView( table );
		pscrollpane.setViewportView( ptable );
		ascrollpane.setViewportView( atable );
		rscrollpane.setViewportView( rtable );
		
		CellEditorListener cel = new CellEditorListener() {
			@Override
			public void editingStopped(ChangeEvent e) {
				currentEdited = true;
			}
			
			@Override
			public void editingCanceled(ChangeEvent e) {
			}
		};
		
		JComboBox	v2combo = new JComboBox( vcombo.getModel() );
		DefaultCellEditor dce = new DefaultCellEditor( v2combo );
		ptable.getColumnModel().getColumn(5).setCellEditor( dce );
		dce.addCellEditorListener( cel );
		
		JComboBox	st2combo = new JComboBox( stcombo.getModel() );
		dce = new DefaultCellEditor( st2combo );
		ptable.getColumnModel().getColumn(6).setCellEditor( dce );
		dce.addCellEditorListener( cel );
		
		ptable.getDefaultEditor( String.class ).addCellEditorListener( cel );
		ptable.getDefaultEditor( Integer.class ).addCellEditorListener( cel );
		
		atable.getDefaultEditor( String.class ).addCellEditorListener( cel );
		atable.getDefaultEditor( Integer.class ).addCellEditorListener( cel );
		rtable.getDefaultEditor( String.class ).addCellEditorListener( cel );
		rtable.getDefaultEditor( Integer.class ).addCellEditorListener( cel );
		
		//ylabel.setBorder( BorderFactory.createLineBorder( Color.red ) );
		
		c.add( vcombo );
		c.add( stcombo );
		
		c.add( afgreida );
		cb.add( afhenda );
		c.add( pantanytt );
		
		c.add( addbtn );
		c.add( rembtn );
		
		c.add( vertbut );
		c.add( excel );
		
		cvorur = new JComponent() {
			/*public void paintComponent( Graphics g ) {
				super.paintComponent(g);
				
				g.setColor( Color.red );
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
				
				System.err.println( this.getWidth() + "  " + this.getHeight() );
			}*/
			
			public void setBounds( int x, int y, int w, int h ) {
				super.setBounds(x, y, w, h);
				
				scrollpane.setBounds( (int)(0.00*w), 70, (int)(1.00*w), (int)(1.0*h)-100 );
				
				ylabel.setBounds( (int)(0.00*w), 20, (int)(0.30*w), 25 );
				label.setBounds( (int)(0.35*w), 20, (int)(0.30*w), 25 );
				plabel.setBounds( (int)(0.70*w), 20, (int)(0.30*w), 25 );
				
				ycombo.setBounds( (int)(0.00*w), 45, (int)(0.30*w), 25 );
				combo.setBounds( (int)(0.35*w), 45, (int)(0.30*w), 25 );
				pcombo.setBounds( (int)(0.70*w), 45, (int)(0.30*w), 25 );
				
				newItem.setBounds( (int)(0.00*w), (int)(1.0*h)-25, (int)(0.40*w), 25 );
				delItem.setBounds( (int)(0.60*w), (int)(1.0*h)-25, (int)(0.40*w), 25 );
				
				vorur.setBounds( (int)(0.00*w), 0, (int)(1.00*w), 25 );
			}
		};
		cvorur.add( scrollpane );
		cvorur.add( ycombo );
		cvorur.add( combo );
		cvorur.add( pcombo );
		cvorur.add( ylabel );
		cvorur.add( label );
		cvorur.add( plabel );
		cvorur.add( vorur );
		cvorur.add( newItem );
		cvorur.add( delItem );
		c.add( cvorur );
		
		cpantanir = new JComponent() {
			public void setBounds( int x, int y, int w, int h ) {
				super.setBounds(x, y, w, h);
				
				pscrollpane.setBounds( (int)(0.00*w), 30, (int)(1.00*w), (int)(1.0*h)-135 );
				pantanir.setBounds( (int)(0.00*w), 0, (int)(1.00*w), 25 );
				scrolled.setBounds( 80, (int)(1.0*h)-100, (int)(1.0*w)-80, 100 );
			}
			
			public void paintComponent( Graphics g ) {
				super.paintComponent(g);
				
				if( image != null ) {
					g.drawImage( image, (int)(0.0*this.getWidth())+4, (int)(1.0*this.getHeight())-100+4, image.getWidth(this)/2, image.getHeight(this)/2, this );
				}
			}
		};
		cpantanir.add( pscrollpane );
		cpantanir.add( pantanir );
		cpantanir.add( scrolled );
		c.add( cpantanir );
		
		cafgreitt = new JComponent() {
			public void setBounds( int x, int y, int w, int h ) {
				super.setBounds(x, y, w, h);
				
				ascrollpane.setBounds( (int)(0.00*w), 30, (int)(1.0*w), (int)(1.0*h)-30 );
				afgreitt.setBounds( (int)(0.0*w), 0, (int)(1.0*w), 25 );
			}
		};
		cafgreitt.add( ascrollpane );
		cafgreitt.add( afgreitt );
		cb.add( cafgreitt );
		
		cafhent = new JComponent() {
			public void setBounds( int x, int y, int w, int h ) {
				super.setBounds(x, y, w, h);
				
				rscrollpane.setBounds( (int)(0.00*w), 30, (int)(1.00*w), (int)(1.0*h)-30 );
				afhent.setBounds( (int)(0.0*w), 0, (int)(1.0*w), 25 );
			}
		};
		cafhent.add( rscrollpane );
		cafhent.add( afhent );
		cb.add( cafhent );
		
		JSplitPane	splitpane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		splitpane.setOneTouchExpandable( true );
		splitpane.setTopComponent( c );
		splitpane.setBottomComponent( cb );
		//splitpane.setDividerLocation(0.0);
		splitpane.setDividerLocation(1200);
		//splitpane.setDividerLocation(1);
		//splitpane.setDividerSize(1200);
		
		final JTabbedPane tpane = new JTabbedPane();
		tpane.addTab("Allt", splitpane);
		tpane.addTab("Vörur", null);
		tpane.addTab("Pantanir", null);
		tpane.addTab("Afgreitt", null);
		tpane.addTab("Afhent", null);
		
		tpane.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int i = tpane.getSelectedIndex();
				if( i == 0 ) {
					//tpane.setComponentAt(1, null);
					if( tpane.indexOfComponent(cvorur) > 0 ) {
						tpane.remove( cvorur );
						tpane.insertTab("Vörur",null,null,null,1);
						c.add( cvorur );
					} 
					if( tpane.indexOfComponent(cpantanir) > 0 ) {
						tpane.remove( cpantanir );
						tpane.insertTab("Pantanir",null,null,null,2);
						c.add( cpantanir );
					} 
					if( tpane.indexOfComponent(cafgreitt) > 0 ) {
						tpane.remove( cafgreitt );
						tpane.insertTab("Afgreitt",null,null,null,3);
						cb.add( cafgreitt );
					} 
					if( tpane.indexOfComponent(cafhent) > 0 ) {
						tpane.remove( cafhent );
						tpane.insertTab("Afhent",null,null,null,4);
						cb.add( cafhent );
					}
				} else if( i == 1 ) {
					c.remove( cvorur );
					tpane.setComponentAt(1, cvorur );
					/*tpane.setComponentAt(1, new JComponent() {
						public void paintComponent( Graphics g ) {
							super.paintComponent( g );
							g.setColor( Color.red );
							g.fillRect(0, 0, this.getWidth(), this.getHeight());
						}
					});*/
					//tpane.getcom
					//cvorur.revalidate();
					//cvorur.repaint();
				} else if( i == 2 ) {
					c.remove( cpantanir );
					tpane.setComponentAt(2, cpantanir);
				} else if( i == 3 ) {
					cb.remove( cafgreitt );
					tpane.setComponentAt(3, cafgreitt);
				} else if( i == 4 ) {
					cb.remove( cafhent );
					tpane.setComponentAt(4, cafhent);
				}
			}
		});
		
		this.add( tpane );
	}
	
	Set<String>	facesTrying = new HashSet<String>();
	final Image getImage( final String name ) {
		String nm = name.split(" ")[0].toLowerCase();
		if( faces.containsKey( name ) ) {
			return faces.get(name);
		} else {
			for( String person : pMap.keySet() ) {
				int val = person.indexOf('>');
				boolean bo = false;
				if( val != -1 ) {
					String pp = person.substring(val+1, person.length());
					if( personMap.containsKey(nm) ) {
						if( personMap.get(nm).equals(pp) ) bo = true;
					} else {
						pp = pp.split(" ")[0];
						pp = pp.toLowerCase();
						pp = pp.replace('ó', 'o');
						pp = pp.replace('ú', 'u');
						pp = pp.replace('á', 'a');
						pp = pp.replace('ð', 'd');
						pp = pp.replace('í', 'i');
						pp = pp.replace('ý', 'y');
						
						if( nm.contains(pp) ) bo = true;
					}
				}
				
				if( bo ) {
					int ind = person.indexOf('"');
					final String link = person.substring(0, ind);
					
					if( !facesTrying.contains( name ) ) {
						facesTrying.add( name );
						new Thread() {
							public void run() {
								try {
									URL url = new URL( "http://www.matis.is/um-matis-ohf/"+link );
									InputStream stream = url.openStream();
									
									String ret = "";
									int r = stream.read(bb);
									while( r > 0 ) {
										ret += new String( bb, 0, r );
										r = stream.read(bb);
									}
									stream.close();
									
									int i = ret.indexOf("<img src=\"");
									if( i >= 0 ) {
										i += 10;
										int e = ret.indexOf("\"", i);
										String urlstr = "http://www.matis.is"+ret.substring(i, e);
										//urlstr = URLEncoder.encode( urlstr, "UTF-8" );
										urlstr = urlstr.replace(" ", "%20");
										
										url = new URL( urlstr );
										Image image = ImageIO.read( url );
										
										faces.put(name, image);
										Order.this.image = image;
										
										//return image;
										cpantanir.repaint();
										facesTrying.remove( name );
									}
									
									/*String[] ss = ret.split("<div class=\"boxbody\">");
									for( int i = 1; i < 2; i++ ) {
										String s = ss[i];
										ind = s.indexOf("></ul>");
										String sub = "<html>"+s.substring(0, ind)+"></ul>";
										sub = sub.replace("</div>", "");
										sub = sub.replace("<img src=\"","<img src=\"http://www.matis.is");
										sub = sub.replace("Sigmar St","Sigmar%20St");
										sub += "</html>";
										
										ed.setEditable( false );
										ed.setContentType("text/html");
										ed.setText( sub );
									}*/
									//ed.setPage( url );
								} catch (MalformedURLException e1) {
									e1.printStackTrace();
								} catch (IOException e2) {
									e2.printStackTrace();
								}
							}
						}.start();
					}
				}
			}
		}
		return null;
	}
	
	protected Vara queryVara() {
		d.setSize(550, 140);
		d.setLocationRelativeTo( this );
		d.setVisible( true );
		
		String str = d.catField.getText();
		if( d.appr && str != null ) {
			long val = 0;
			try {
				val = Long.parseLong( str );
			} catch( Exception e ) {
				e.printStackTrace();
			}
			if( val != 0 ) return new Vara( d.nameField.getText(), (String)d.frmlCombo.getSelectedItem(), (String)d.brgrCombo.getSelectedItem(), val );
		}
		
		return null;
	}
	
	protected Vara newItem() throws SQLException {
		Vara v = queryVara();
		
		if( v != null ) {
			String ord = "'"+v.Nafn+"','"+v.Framleiðandi+"','"+v.Byrgir+"',"+v._Cat+",'"+user+"'";
			String sql = "insert into [order].[dbo].[Vara] values ("+ord+")";
			
			PreparedStatement 	ps = con.prepareStatement(sql);
			boolean				b = ps.execute();
			
			if( !b ) {
				ordlist.add( v );
				
				int i;
				String str = null;
				for( i = 0; i < combo.getItemCount(); i++ ) {
					str = (String)combo.getItemAt(i);
					if( str.compareTo(v.Framleiðandi) >= 0 ) break;
				}
					
				if( !str.equals(v.Framleiðandi) ) {
					combo.insertItemAt(v.Framleiðandi, i);
				}
				
				str =null;
				for( i = 0; i < pcombo.getItemCount(); i++ ) {
					str = (String)pcombo.getItemAt(i);
					if( str.compareTo(v.Byrgir) >= 0 ) break;
				}
				
				if( !str.equals(v.Byrgir) ) {
					pcombo.insertItemAt(v.Byrgir, i);
				}
				
				int mr = model.getRowCount()-1;
				modelRowMap.put( (String)model.getValueAt( mr, 0), mr );
				
				myVars.add( v.Nafn );
				table.tableChanged( new TableModelEvent(model) );
			}
			
			ps.close();
		}
		
		return v;
	}
	
	protected boolean delItem( int cat ) throws SQLException {
		String sql = "delete from [order].[dbo].[Vara] where cat = "+cat;
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		boolean				b = ps.execute();
		
		ps.close();
		
		return b;
	}

	public class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
		 final JSpinner spinner = new JSpinner();
		
		 // Initializes the spinner.
		 public SpinnerEditor(String[] items) {
		     spinner.setModel(new SpinnerListModel(java.util.Arrays.asList(items)));
		 }
		
		 // Prepares the spinner component and returns it.
		 public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		     spinner.setValue(value);
		     return spinner;
		 }
		
		 // Enables the editor only for double-clicks.
		 public boolean isCellEditable(EventObject evt) {
		     if (evt instanceof MouseEvent) {
		         return ((MouseEvent)evt).getClickCount() >= 2;
		     }
		     return true;
		 }
		
		 // Returns the spinners current value.
		 public Object getCellEditorValue() {
		     return spinner.getValue();
		 }
	}

	public final Color b1 = new Color( 0,100,255 );
	public final Color b2 = new Color( 200,200,255 );
	
	public void paint( Graphics g ) {
		super.paint( g );
	}
}
