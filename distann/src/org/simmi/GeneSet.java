package org.simmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GeneSet {
	/**
	 * @param args
	 * @throws IOException 
	 */
	
	static Map<Character,Character>	sidechainpolarity = new HashMap<Character,Character>();
	static Map<Character,Integer>	sidechaincharge = new HashMap<Character,Integer>();
	static Map<Character,Double>	hydropathyindex = new HashMap<Character,Double>();
	static Map<Character,Double>	aamass = new HashMap<Character,Double>();
	static Map<Character,Double>	isoelectricpoint = new HashMap<Character,Double>();
	//abundance
	//aliphatic - aromatic
	//size
	//sortcoeff
	
	static class Erm implements Comparable<Erm> {
		double	d;
		char	c;
		
		Erm( double d, char c ) {
			this.d = d;
			this.c = c;
		}

		@Override
		public int compareTo(Erm o) {
			double mis = d - o.d;
			
			return mis > 0 ? 1 : (mis < 0 ? -1 : 0);
		}
	};
	static List<Erm>	uff = new ArrayList<Erm>();
	static List<Erm>	uff2 = new ArrayList<Erm>();
	static List<Erm>	uff3 = new ArrayList<Erm>();
	static List<Erm>	mass = new ArrayList<Erm>();
	static List<Erm>	isoel = new ArrayList<Erm>();
	
	static {
		sidechainpolarity.put('A', 'n');
		sidechainpolarity.put('R', 'P');
		sidechainpolarity.put('N', 'P');
		sidechainpolarity.put('D', 'P');
		sidechainpolarity.put('C', 'n');
		sidechainpolarity.put('E', 'P');
		sidechainpolarity.put('Q', 'P');
		sidechainpolarity.put('G', 'n');
		sidechainpolarity.put('H', 'P');
		sidechainpolarity.put('I', 'n');
		sidechainpolarity.put('L', 'n');
		sidechainpolarity.put('K', 'P');
		sidechainpolarity.put('M', 'n');
		sidechainpolarity.put('F', 'n');
		sidechainpolarity.put('P', 'n');
		sidechainpolarity.put('S', 'P');
		sidechainpolarity.put('T', 'P');
		sidechainpolarity.put('W', 'n');
		sidechainpolarity.put('Y', 'P');
		sidechainpolarity.put('V', 'n');
		
		sidechaincharge.put('A', 0);
		sidechaincharge.put('R', 1);
		sidechaincharge.put('N', 0);
		sidechaincharge.put('D', -1);
		sidechaincharge.put('C', 0);
		sidechaincharge.put('E', -1);
		sidechaincharge.put('Q', 0);
		sidechaincharge.put('G', 0);
		sidechaincharge.put('H', 0);
		sidechaincharge.put('I', 0);
		sidechaincharge.put('L', 0);
		sidechaincharge.put('K', 1);
		sidechaincharge.put('M', 0);
		sidechaincharge.put('F', 0);
		sidechaincharge.put('P', 0);
		sidechaincharge.put('S', 0);
		sidechaincharge.put('T', 0);
		sidechaincharge.put('W', 0);
		sidechaincharge.put('Y', 0);
		sidechaincharge.put('V', 0);
		
		hydropathyindex.put('A', 1.8);
		hydropathyindex.put('R', -4.5);
		hydropathyindex.put('N', -3.5);
		hydropathyindex.put('D', -3.5);
		hydropathyindex.put('C', 2.5);
		hydropathyindex.put('E', -3.5);
		hydropathyindex.put('Q', -3.5);
		hydropathyindex.put('G', -0.4);
		hydropathyindex.put('H', -3.2);
		hydropathyindex.put('I', 4.5);
		hydropathyindex.put('L', 3.8);
		hydropathyindex.put('K', -3.9);
		hydropathyindex.put('M', 1.9);
		hydropathyindex.put('F', 2.8);
		hydropathyindex.put('P', -1.6);
		hydropathyindex.put('S', -0.8);
		hydropathyindex.put('T', -0.7);
		hydropathyindex.put('W', -0.9);
		hydropathyindex.put('Y', -1.3);
		hydropathyindex.put('V', 4.2);
		
		aamass.put('A', 89.09404 );
		aamass.put('C', 121.15404 );
		aamass.put('D', 133.10384 );
		aamass.put('E', 147.13074 );
		aamass.put('F', 165.19184 );
		aamass.put('G', 75.06714 );
		aamass.put('H', 155.15634 );
		aamass.put('I', 131.17464 );
		aamass.put('K', 146.18934 );
		aamass.put('L', 131.17464 );
		aamass.put('M', 149.20784 );
		aamass.put('N', 132.11904 );
		aamass.put('O', 100.0 );
		aamass.put('P', 115.13194 );
		aamass.put('Q', 146.14594 );
		aamass.put('R', 174.20274 );
		aamass.put('S', 105.09344 );
		aamass.put('T', 119.12034 );
		aamass.put('U', 168.053 );
		aamass.put('V', 117.14784 );
		aamass.put('W', 204.22844 );
		aamass.put('Y', 181.19124 );
		
		isoelectricpoint.put('A', 6.01 );
		isoelectricpoint.put('C', 5.05 );
		isoelectricpoint.put('D', 2.85 );
		isoelectricpoint.put('E', 3.15 );
		isoelectricpoint.put('F', 5.49 );
		isoelectricpoint.put('G', 6.06 );
		isoelectricpoint.put('H', 7.6 );
		isoelectricpoint.put('I', 6.05 );
		isoelectricpoint.put('K', 9.6 );
		isoelectricpoint.put('L', 6.01 );
		isoelectricpoint.put('M', 5.74 );
		isoelectricpoint.put('N', 5.41 );
		isoelectricpoint.put('O', 21.0 );
		isoelectricpoint.put('P', 6.3 );
		isoelectricpoint.put('Q', 5.65 );
		isoelectricpoint.put('R', 10.76 );
		isoelectricpoint.put('S', 5.68 );
		isoelectricpoint.put('T', 5.6 );
		isoelectricpoint.put('U', 20.0 );
		isoelectricpoint.put('V', 6.0 );
		isoelectricpoint.put('W', 5.89 );
		isoelectricpoint.put('Y', 5.64 );
		
		for( char c : hydropathyindex.keySet() ) {
			double d = hydropathyindex.get(c);
			uff.add( new Erm( d, c ) );
		}
		Collections.sort( uff );
		
		for( char c : sidechainpolarity.keySet() ) {
			double d = sidechainpolarity.get(c);
			uff2.add( new Erm( d, c ) );
		}
		Collections.sort( uff2 );
		
		for( char c : sidechaincharge.keySet() ) {
			double d = sidechaincharge.get(c);
			uff3.add( new Erm( d, c ) );
		}
		Collections.sort( uff3 );
		
		for( char c : aamass.keySet() ) {
			double d = aamass.get(c);
			mass.add( new Erm( d, c ) );
		}
		Collections.sort( mass );
		
		for( char c : isoelectricpoint.keySet() ) {
			double d = isoelectricpoint.get(c);
			isoel.add( new Erm( d, c ) );
		}
		Collections.sort( isoel );
	}
	
	static Map<String,String>		swapmap = new HashMap<String,String>();
	public static void func1( String[] names, File dir ) throws IOException {
		Map<String,String>		allgenes = new HashMap<String,String>();
		Map<String,Set<String>>	geneset = new HashMap<String,Set<String>>();
		
		PrintStream ps = new PrintStream("/home/sigmar/iron.giant");
		System.setErr( ps );
		
		for( String name : names ) {
			File 			f = new File( dir, name );
			Set<String>		set = new HashSet<String>();
			
			FileReader		fr = new FileReader( f );
			BufferedReader 	br = new BufferedReader( fr );
			String line = br.readLine();
			while( line != null ) {
				String trim = line.trim();
				if( trim.startsWith(">ref") || trim.startsWith(">sp") || trim.startsWith(">pdb") || trim.startsWith(">dbj") || trim.startsWith(">gb") || trim.startsWith(">emb") ) {
					String[] split = trim.split("\\|");
					set.add( split[1] );
					
					if( !allgenes.containsKey( split[1] ) || allgenes.get( split[1] ) == null ) {
						allgenes.put( split[1], split.length > 1 ? split[2].trim() : null );
					}
				}
				
				line = br.readLine();
			}
			
			System.err.println( name + " genes total: " + set.size() );
			geneset.put( name, set );
		}
		
		Set<String>	allset = new HashSet<String>( allgenes.keySet() );
		for( String name : geneset.keySet() ) {
			Set<String>	gset = geneset.get( name );
			allset.retainAll( gset );
		}
		System.err.println( "Core genome size: " + allset.size() );
		
		Set<String>	nameset = null;
		
		/*for( String gname : allset ) {
			System.err.println( gname + "\t" + allgenes.get(gname) );
		}*/
		
		for( String aname : names ) {
			allset = new HashSet<String>( allgenes.keySet() );
			nameset = new HashSet<String>( Arrays.asList(names) );
			nameset.remove(aname);
			for( String tname : nameset ) {
				allset.removeAll( geneset.get(tname) );
			}
			System.err.println( "Genes found only in " + swapmap.get(aname) + "\t" + allset.size() );
			for( String gname : allset ) {
				System.err.println( gname + "\t" + allgenes.get(gname) );
			}
		}
		
		for( String aname : names ) {
			allset = new HashSet<String>( allgenes.keySet() );
			nameset = new HashSet<String>( Arrays.asList(names) );
			nameset.remove(aname);
			allset.removeAll( geneset.get(aname) );
			for( String tname : nameset ) {
				allset.retainAll( geneset.get(tname) );
			}
			
			Set<String>	reset = new HashSet<String>();
			for( String name : nameset ) {
				reset.add( swapmap.get(name) );
			}
			System.err.println( "Genes only in all off " + reset + "\t" + allset.size() );
			for( String gname : allset ) {
				System.err.println( gname + "\t" + allgenes.get(gname) );
			}
		}
		
		for( int i = 0; i < names.length; i++ ) {
			for( int y = i+1; y < names.length; y++ ) {
				allset = new HashSet<String>( allgenes.keySet() );
				nameset = new HashSet<String>( Arrays.asList(names) );
				//nameset.add( names[i] );
				//nameset.add( names[y] );
				nameset.remove( names[i] );
				nameset.remove( names[y] );
				
				allset.removeAll( geneset.get(names[i]) );
				allset.removeAll( geneset.get(names[y]) );
				for( String tname : nameset ) {
					allset.retainAll( geneset.get(tname) );
				}
				
				Set<String>	reset = new HashSet<String>();
				//reset.add( swapmap.get(names[i]) );
				//reset.add( swapmap.get(names[y]) );
				
				for( String name : nameset ) {
					reset.add( swapmap.get(name) );
				}
				System.err.println( "Genes only in all of " + reset + "\t" + allset.size() );
				for( String gname : allset ) {
					System.err.println( gname + "\t" + allgenes.get(gname) );
				}
			}
		}
		
		for( int i = 0; i < names.length; i++ ) {
			for( int y = i+1; y < names.length; y++ ) {
				allset = new HashSet<String>( allgenes.keySet() );
				nameset = new HashSet<String>( Arrays.asList(names) );
				//nameset.add( names[i] );
				//nameset.add( names[y] );
				nameset.remove( names[i] );
				nameset.remove( names[y] );
				
				allset.retainAll( geneset.get(names[i]) );
				allset.retainAll( geneset.get(names[y]) );
				for( String tname : nameset ) {
					allset.removeAll( geneset.get(tname) );
				}
				
				Set<String>	reset = new HashSet<String>();
				reset.add( swapmap.get(names[i]) );
				reset.add( swapmap.get(names[y]) );
				
				/*for( String name : nameset ) {
					reset.add( swapmap.get(name) );
				}*/
				System.err.println( "Genes only in all of " + reset + "\t" + allset.size() );
				for( String gname : allset ) {
					System.err.println( gname + "\t" + allgenes.get(gname) );
				}
			}
		}
		
		for( int i = 0; i < names.length; i++ ) {
			for( int y = i+1; y < names.length; y++ ) {
				for( int k = y+1; k < names.length; k++ ) {
					allset = new HashSet<String>( allgenes.keySet() );
					nameset = new HashSet<String>( Arrays.asList(names) );
					//nameset.add( names[i] );
					//nameset.add( names[y] );
					nameset.remove( names[i] );
					nameset.remove( names[y] );
					nameset.remove( names[k] );
					
					allset.removeAll( geneset.get(names[i]) );
					allset.removeAll( geneset.get(names[y]) );
					allset.removeAll( geneset.get(names[k]) );
					for( String tname : nameset ) {
						allset.retainAll( geneset.get(tname) );
					}
					
					Set<String>	reset = new HashSet<String>();
					/*reset.add( swapmap.get(names[i]) );
					reset.add( swapmap.get(names[y]) );
					reset.add( swapmap.get(names[k]) );*/
					
					for( String name : nameset ) {
						reset.add( swapmap.get(name) );
					}
					
					System.err.println( "Genes only in all of " + reset + "\t" + allset.size() );
					for( String gname : allset ) {
						System.err.println( gname + "\t" + allgenes.get(gname) );
					}
				}
			}
		}
		
		System.err.println( "Unique genes total: " + allgenes.size() );
		
		ps.close();
	}
	
	static Map<String,String>	aas = new HashMap<String,String>();
	public static void loci2aasequence( String[] stuff, File dir2 ) throws IOException {
		for( String st : stuff ) {
			File aa = new File( dir2, st+".fsa" );
			BufferedReader br = new BufferedReader( new FileReader(aa) );
			String line = br.readLine();
			String name = null;
			String ac = "";
			while( line != null ) {
				if( line.startsWith(">") ) {
					if( ac.length() > 0 ) aas.put(swapmap.get(st+".out")+" "+name, ac);
					
					ac = "";
					name = line.substring(1).split(" ")[0];
				} else ac += line.trim();
				line = br.readLine();
			}
			br.close();
		}
	}
	
	public static void printnohits( String[] stuff, File dir, File dir2 ) throws IOException {
		loci2aasequence(stuff, dir2);
		for( String st : stuff ) {
			System.err.println("Unknown genes in " + swapmap.get(st+".out"));
			
			File ba = new File( dir, st+".out" );
			BufferedReader br = new BufferedReader( new FileReader(ba) );
			String line = br.readLine();
			String name = null;
			//String ac = null;
			while( line != null ) {
				if( line.startsWith("Query=") ) {
					name = line.substring(7).split(" ")[0];
				}
				
				if( line.contains("No hits") ) {
					System.err.println( name + "\t" + aas.get(name) );
				}
				
				line = br.readLine();
			}
			br.close();
		}
	}
	
	public static void func3( String[] names, File dir ) throws IOException {
		FileWriter	fw = new FileWriter( new File( dir, "all.fsa" ) );
		for( String name : names ) {
			File 			f = new File( dir, name+".fsa" );
			BufferedReader 	br = new BufferedReader( new FileReader( f ) );
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) fw.write( ">"+swapmap.get(name+".out")+" "+line.substring(1)+"\n" );
				else fw.write( line+"\n" );
				
				line = br.readLine();
			}
			br.close();
		}
		fw.close();
	}
	
	public static void func4( File dir, String[] stuff ) throws IOException {
		Set<Set<String>>	total = new HashSet<Set<String>>();
		for( String name : stuff ) {
			File f = new File( dir, name+"all.out" );
			BufferedReader	br = new BufferedReader( new FileReader( f ) );
			
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith("Sequences prod") ) {
					line = br.readLine();
					Set<String>	all = new HashSet<String>();
					while( line != null && !line.startsWith(">") ) {
						String trim = line.trim();
						if( trim.startsWith("scoto") ) {
							String val = trim.substring( 0, trim.indexOf('#')-1 );
							all.add( val );
						}						
						line = br.readLine();
					}
					
					boolean cont = false;
					for( Set<String>	check : total ) {
						for( String aval : all ) {
							if( check.contains(aval) ) {
								cont = true;
								check.addAll( all );
								break;
							}
						}
					}
					if( !cont ) total.add( all );
					
					if( line == null ) break;
				}
				
				line = br.readLine();
			}
		}
		
		HashMap<Set<String>,Set<Map<String,Set<String>>>>	clusterMap = new HashMap<Set<String>,Set<Map<String,Set<String>>>>();
		
		for( Set<String>	t : total ) {
			Set<String>	teg = new HashSet();
			for( String e : t ) {
				String str = e.substring( 0, e.indexOf(' ') );
				teg.add( str );
			}
			
			Set<Map<String,Set<String>>>	setmap;
			if( clusterMap.containsKey( teg ) ) {
				setmap = clusterMap.get( teg );
			} else {
				setmap = new HashSet<Map<String,Set<String>>>();
				clusterMap.put( teg, setmap );
			}
			
			Map<String,Set<String>>	submap = new HashMap<String,Set<String>>();
			setmap.add( submap );
			
			for( String e : t ) {
				String str = e.substring( 0, e.indexOf(' ') );
				Set<String>	set;
				if( submap.containsKey( str ) ) {
					set = submap.get(str);
				} else {
					set = new HashSet<String>();
					submap.put( str, set );	
				}
				set.add( e );
			}
		}
		
		PrintStream ps = new PrintStream( new FileOutputStream("/home/sigmar/out.out") );
		System.setErr( ps );
		for( Set<String> set : clusterMap.keySet() ) {
			Set<Map<String,Set<String>>>	setmap = clusterMap.get( set );
			
			int i = 0;
			for( Map<String,Set<String>>	map : setmap ) {
				for( String s: map.keySet() ) {
					Set<String>	genes = map.get(s);
					i += genes.size();
				}
			}
			System.err.println( "Included in : " + set.size() + " scotos " + set + " containing: " + setmap.size() + "set of genes\ttotal of " + i + " loci" );
			
			i = 0;
			for( Map<String,Set<String>>	map : setmap ) {
				System.err.println("Starting set " + i);
				
				for( String s: map.keySet() ) {
					Set<String>	genes = map.get(s);
					
					System.err.println( "In " + s + " containing " + genes.size() );
					for( String gene : genes ) {
						System.err.println( gene + "\t" + lociMap.get(gene) );
					}
				}
				System.err.println();
				
				i++;
			}
		}
		ps.close();
		
		/*System.err.println( "# clusters: " + total.size() );
		int max = 0;
		for( Set<String> sc : total ) {
			if( sc.size() > max ) max = sc.size();
			System.err.println( "\tcluster size: " + sc.size() );
		}
		System.err.println( "maxsize: " + max );
		
		int[]	ia = new int[max];
		for( Set<String> sc : total ) {
			ia[sc.size()-1]++;
		}
		
		for( int i : ia ) {
			System.err.println( "hist: " + i );
		}*/
	}
	
	static Map<String,String>	lociMap = new HashMap<String,String>();
	public static void loci2gene( String[] stuff, File dir ) throws IOException {
		//Map<String,String>	aas = new HashMap<String,String>();
		for( String st : stuff ) {			
			File ba = new File( dir, st+".out" );
			BufferedReader br = new BufferedReader( new FileReader(ba) );
			String line = br.readLine();
			String name = null;
			while( line != null ) {
				if( line.startsWith("Query= ") ) {
					name = line.substring(7).split(" ")[0];
				}
				
				String prename = swapmap.get(st+".out")+" "+name;
				if( line.contains("No hits") ) {
					lociMap.put( prename, "No match\t"+aas.get(prename) );
					//System.err.println( prename + "\tNo match" );
				}
				
				if( line.startsWith(">ref") || line.startsWith(">sp") || line.startsWith(">pdb") || line.startsWith(">dbj") || line.startsWith(">gb") || line.startsWith(">emb") ) {
					String[] split = line.split("\\|");
					lociMap.put( prename, split[1] + (split.length > 2 ? "\t" + split[2] : "") );
					//System.err.println( prename + "\t" + split[1] );
				}
				
				line = br.readLine();
			}
			br.close();
		}
	}
	
	public static void aahist( File f1, File f2 ) throws IOException {
		Map<Character,Long>	aa1map = new HashMap<Character,Long>();
		Map<Character,Long>	aa2map = new HashMap<Character,Long>();
		
		long t1 = 0;
		FileReader fr = new FileReader( f1 );
		BufferedReader br = new BufferedReader( fr );
		String line = br.readLine();
		while( line != null ) {
			if( !line.startsWith(">") ) {
				for( int i = 0; i < line.length(); i++ ) {
					char c = line.charAt(i);
					if( aa1map.containsKey(c) ) {
						aa1map.put( c, aa1map.get(c)+1L );
					} else aa1map.put( c, 1L );
					
					t1++;
				}
			}
			line = br.readLine();
		}
		br.close();
		
		long t2 = 0;
		fr = new FileReader( f2 );
		br = new BufferedReader( fr );
		line = br.readLine();
		while( line != null ) {
			if( !line.startsWith(">") ) {
				for( int i = 0; i < line.length(); i++ ) {
					char c = line.charAt(i);
					if( aa2map.containsKey(c) ) {
						aa2map.put( c, aa2map.get(c)+1L );
					} else aa2map.put( c, 1L );
					
					t2++;
				}
			}
			line = br.readLine();
		}
		br.close();
		
		
		for( Erm e : mass ) {
			char c = e.c;
			if( aa1map.containsKey( c ) ) {
				System.err.println( e.d + "\t" + c + "\t" + (aa1map.get(c)/(double)t1) + "\t" + (aa2map.containsKey(c) ? (aa2map.get(c)/(double)t2) : "-") );
			}
		}	
	}
	
	public static void main(String[] args) {
		String[]	stuff = {"aa1","aa2","aa4","aa6","aa7","aa8"};
		String[]	names = {"aa1.out","aa2.out","aa4.out","aa6.out","aa7.out","aa8.out"};
		File dir = new File("/home/sigmar/thermus/results/");
		File dir2 = new File("/home/sigmar/thermus/out/");
		
		swapmap.put("aa1.out", "scoto_346");
		swapmap.put("aa2.out", "scoto_2101");
		swapmap.put("aa4.out", "scoto_2127");
		swapmap.put("aa6.out", "scoto_252");
		swapmap.put("aa7.out", "scoto_1572");
		swapmap.put("aa8.out", "scoto_4063");
		
		try {
			//func1( names, dir );
			//func2( stuff, dir, dir2 );
			//func3( stuff, dir2 );
			
			//loci2aasequence(stuff, dir2);
			//loci2gene( stuff, dir );
			//func4( dir2, stuff );
			
			aahist( new File("/home/sigmar/thermus/out/aa2.fsa"), new File("/home/sigmar/nc.aa") );
			//aahist( new File("/home/sigmar/thermus/out/aa2.fsa"), new File("/home/sigmar/thermus/hb27.aa") );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
}