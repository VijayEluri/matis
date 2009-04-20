package org.simmi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TooManyListenersException;

import javax.management.timer.Timer;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class HabitsPanel extends JSplitPane {
	int 						min = 0;
	final Color 				paleGreen = new Color( 20,230,60,96 ); 
	List<List<String>>	eatList = new ArrayList<List<String>>();
	
	public HabitsPanel( String lang ) {
		super( JSplitPane.VERTICAL_SPLIT );
		
		JTabbedPane timelineTabPane = new JTabbedPane( JTabbedPane.RIGHT );
		final JTable		timelineTable = new JTable() {
			public Component prepareRenderer( TableCellRenderer renderer, int row, int column ) {
				Component c = super.prepareRenderer( renderer, row, column );
				//Color bc = c.getBackground();
				Object val = this.getValueAt(0, column);
				if( val.equals("Sat") || val.equals("Sun") ) {
					c.setBackground( paleGreen );
				} else if( this.getSelectedRow() != row ) {
					c.setBackground( Color.white );
				} else {
					c.setBackground( this.getSelectionBackground() );
				}
				return c;
			}
		};
		JScrollPane timelineScroll = new JScrollPane(timelineTable);
		
		final JTable		timelineDataTable = new JTable() {
			@Override
			public Component prepareRenderer( TableCellRenderer renderer, int row, int column ) {
				Component c = super.prepareRenderer( renderer, row, column );
				//Color bc = c.getBackground();
				Object val = timelineTable.getValueAt(0, column);
				if( val.equals("Sat") || val.equals("Sun") ) {
					c.setBackground( paleGreen );
				} else if( this.getSelectedRow() != row ) {
					c.setBackground( Color.white );
				} else {
					c.setBackground( this.getSelectionBackground() );
				}
				return c;
			}
		};
		final JScrollPane timelineDataScroll = new JScrollPane( timelineDataTable );
		
		DropTarget dropTarget = new DropTarget() {
			public boolean isActive() {
				return true;
			}
		};
		timelineDataScroll.setDropTarget( dropTarget );
		
		JSplitPane	timelineSplit = new JSplitPane( JSplitPane.VERTICAL_SPLIT, timelineScroll, timelineDataScroll );
		
		if( lang.equals("IS") ) {
			timelineTabPane.addTab("Inn", timelineSplit);
			timelineTabPane.addTab("Út", null);
		} else {
			timelineTabPane.addTab("In", timelineSplit);
			timelineTabPane.addTab("Out", null);
		}
		
		Calendar now = Calendar.getInstance();
		final Calendar cal = new GregorianCalendar();
		cal.set( Calendar.YEAR, now.get( Calendar.YEAR ) );
		cal.set( Calendar.MONTH, 0 );
		cal.set( Calendar.DAY_OF_MONTH, 1 );
		final long time = cal.getTimeInMillis();
		
		//Date d = new Date(Date.);
		TableModel	timelineModel = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener arg0) {
				
			}

			@Override
			public Class<?> getColumnClass(int arg0) {
				return String.class;
			}

			@Override
			public int getColumnCount() {
				// TODO Auto-generated method stub
				return 365;
			}

			@Override
			public String getColumnName(int arg0) {
				cal.setTimeInMillis( time+arg0*Timer.ONE_DAY );
				return cal.get( Calendar.WEEK_OF_YEAR ) + "";
			}

			@Override
			public int getRowCount() {
				// TODO Auto-generated method stub
				return 2;
			}

			@Override
			public Object getValueAt(int arg0, int arg1) {
				cal.setTimeInMillis( time+arg1*Timer.ONE_DAY );
				String str;
				if( arg0 == 1 ) str = cal.get(Calendar.DAY_OF_MONTH) + ". "+cal.getDisplayName( Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
				else str = cal.getDisplayName( Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
				return str;
			}

			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValueAt(Object arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
		};
		timelineTable.setModel( timelineModel );
		timelineTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		
		for( int i = 0; i < 365; i++ ) {
			eatList.add(null);
		}
		
		TableModel	timelineDataModel = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			@Override
			public int getColumnCount() {
				return 365;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return null;
			}

			@Override
			public int getRowCount() {
				// TODO Auto-generated method stub
				return min;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				List<String> dayList = eatList.get(columnIndex);
				if( dayList != null && rowIndex < dayList.size() ) {
					return dayList.get(rowIndex);
				}
				return null;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				
			}
		};
		timelineDataTable.setModel( timelineDataModel );
		timelineDataTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		
		try {
			dropTarget.addDropTargetListener( new DropTargetListener(){
			
				@Override
				public void dropActionChanged(DropTargetDragEvent dtde) {
					// TODO Auto-generated method stub
					
				}
			
				@Override
				public void drop(DropTargetDropEvent dtde) {
					Point loc = dtde.getLocation();
					Point offset = timelineDataScroll.getViewport().getViewPosition();
					Point p = new Point( offset.x + loc.x, offset.y + loc.y );
					int c = timelineDataTable.columnAtPoint( p );
					System.err.println( c );
					List<String> list = eatList.get(c);
					if( list == null ) {
						list = new ArrayList<String>();
						eatList.set( c, list );
					}
					String val;
					try {
						val = dtde.getTransferable().getTransferData( DataFlavor.stringFlavor ).toString();
						if( val != null ) {
							String[] spl = val.split("\n");
							for( String str : spl ) {
								String[] subspl = str.split("\t");
								if( subspl.length > 1 ) list.add( subspl[1] );
							}
						}
						if( list.size() > min ) {
							min = list.size();
						}
						
						timelineDataTable.revalidate();
						timelineDataTable.repaint();
					} catch (UnsupportedFlavorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
				@Override
				public void dragOver(DropTargetDragEvent dtde) {
					// TODO Auto-generated method stub
					
				}
			
				@Override
				public void dragExit(DropTargetEvent dte) {
					// TODO Auto-generated method stub
					
				}
			
				@Override
				public void dragEnter(DropTargetDragEvent dtde) {
					// TODO Auto-generated method stub
					
				}
			});
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JComponent drawer = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
			}
		};
		JScrollPane timelineDrawScroll = new JScrollPane( drawer );
		
		this.setTopComponent( timelineTabPane );
		this.setBottomComponent( timelineDrawScroll );
	}
}