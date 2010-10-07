package org.simmi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.transform.FastFourierTransformer;

public class Fft extends JComponent {

	BufferedImage	bi;
	int				i;
	Graphics2D		ig2;
	Timer			timer;
	
	public Fft() {		
		timer = new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Fft.this.repaint();
			}
		});
		timer.setCoalesce( false );
		
		i = 0;
	}
	
	private AudioFormat getFormat() {
	    float sampleRate = 44100;
	    int sampleSizeInBits = 16;
	    int channels = 1;
	    boolean signed = true;
	    boolean bigEndian = true;
	    return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}
	
	double[]	dd = null;
	public void record() {
		FastFourierTransformer fft = new FastFourierTransformer();
		
		final AudioFormat format = getFormat();
	    DataLine.Info info = new DataLine.Info( TargetDataLine.class, format );
		try {			
			TargetDataLine line = (TargetDataLine)AudioSystem.getLine(info);
			line.open(format);
		    line.start();
		     
		    //int length = (int)data.length;
		    //ByteBuffer bb = new Pointer( data.buffer ).getByteBuffer(0, 8*length);
		      
		    //int bufferSize = (int)format.getSampleRate() * format.getFrameSize();
	        ByteBuffer buffer = ByteBuffer.allocate( 8192 );
	        ShortBuffer	sb = buffer.asShortBuffer();
	        
	        if( dd == null ) {
	        	dd = new double[ sb.limit() ];
	        }
	        
	        int k = /*((1<<8)-1)<<16;*/ (1<<25)-1;
		    while( true ) {
		    	int r = line.read( buffer.array(), 0, buffer.limit() );
			    for( int b = 0; b < 4096; b++ ) {
			    	//if( i >= db.limit() ) System.err.println( db.limit() + "   " + i );
			    	int rgb = sb.get(b);
			    	dd[b] = rgb;
			    	//max = Math.max( max, Math.abs(rgb) );
			    }
			    Complex[] cc = fft.transform( dd );
			    double max = 0;
			    for( int b = 0; b < 4096; b++ ) {
			    	dd[b] = Math.sqrt( cc[b].getReal()*cc[b].getReal() + cc[b].getImaginary()*cc[b].getImaginary() );
			    	max = Math.max( max, dd[b] );
			    }
			    
			    for( int b = 0; b < 512; b++ ) {
			    	double total = 0.0;
			    	for( int v = 0; v < 2; v++ ) {
			    		total += dd[b*2+v];
			    	}
			    	int u = (int)((total*128)/max);
			    	bi.setRGB(i, b, k-(u<<16)-(u<<8) );
			    }
			    
			    i = (i+1)%bi.getWidth();
			    
			    this.repaint();
		    }
		    //line.stop();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		//timer.start();
		new Thread() {
			public void run() {
				record();
			}
		}.start();
	}
	
	Random r = new Random();
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		
		if( bi != null ) {
			/*for( int y = 0; y < bi.getHeight(); y++ ) {
				int rgb = r.nextInt(255);
				//System.err.println( i + "  " + y );
				bi.setRGB(i, y, rgb);
			}*/
			g.drawImage(bi, 0, 0, this);
			
			//i = (i+1)%512;
		}
	}
	
	public void init() {
		bi = (BufferedImage)createImage(1024, 512);
		ig2 = bi.createGraphics();
		ig2.setColor( Color.white );
		ig2.fillRect( 0, 0, bi.getWidth(), this.getHeight() );
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Fft fft = new Fft();		
		JFrame	frame = new JFrame();
		frame.setSize( 1024,512 );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.add( fft );
		frame.setVisible( true );
		fft.init();
		fft.start();
		
		//double[] data = new double[64];
		//FastFourierTransformer	fft = new FastFourierTransformer();
		//Complex[] cmp = fft.transform(data);
	}
}
