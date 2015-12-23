import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DataSet {
	public DataSet(){
		
	}
	
	public static void main(String args[]) throws IOException{
		String path0 = "/Users/yanjingchen/Desktop/bigdata-project/Dataset/ydata-user.txt";
		String path1 = "/Users/yanjingchen/Desktop/bigdata-project/Dataset/ydata-f.txt";
		String path2 = "/Users/yanjingchen/Desktop/bigdata-project/Dataset/ydata-m.txt";
		String path3 = "/Users/yanjingchen/Desktop/bigdata-project/Dataset/ydata-movie-test.txt";
		ArrayList<String> f = new ArrayList<String>();
		ArrayList<String> m = new ArrayList<String>();
 		
		File filename0 = new File(path0);
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(filename0));
		} catch (FileNotFoundException e) {
			System.out.println("File reading failed");
			System.exit(-1);
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(reader);
		String line = "";
		try {
			line = br.readLine();
			while(line != null){
//				System.out.println(line.length());
				line = line.trim();
				String[] item = line.split("\\t");
//				System.out.println(item.length);
//				for(int i = 0;i<item.length;++i){
//					System.out.println(item[i]);
//				}
				String id = item[0];
				String gender = item[2];
				if (gender.equals("f")){
					f.add(id);
				}
				else{
					if(gender.equals("m")){
						m.add(id);
					}
					else {
						f.add(id);
						m.add(id);
					}
				}
				
				line = br.readLine();
			}
		} catch (IOException e) {
			System.out.println("Can't read file line!");
			System.exit(-1);
			e.printStackTrace();
		}
		
//		for (int i = 0; i < f.size(); i++) {
//			System.out.print(f.get(i)+", ");
//		}
//		System.out.println();
//		for (int i = 0; i < m.size(); i++) {
//			System.out.print(m.get(i)+", ");
//		}
		
		FileWriter f1 = new FileWriter(path1,true);
		FileWriter m1 = new FileWriter(path2,true);
		BufferedWriter out1=null;
		BufferedWriter out2 = null;
		out1 = new BufferedWriter(f1);
		out2 = new BufferedWriter(m1);
		
		File filename3 = new File(path3);
		InputStreamReader reader3 = null;
		try {
			reader3 = new InputStreamReader(new FileInputStream(filename3));
		} catch (FileNotFoundException e) {
			System.out.println("File reading failed");
			System.exit(-1);
			e.printStackTrace();
		}
		BufferedReader br3 = new BufferedReader(reader3);
		String line3 = "";
		try {
			line3 = br3.readLine();
			while(line3!=null){
				String[] tmp3 = line3.split("\\t");
				String id = tmp3[0];
				String movie = tmp3[1];
				String score1 = tmp3[2];
				String score2 = tmp3[3];
				String string ="";
				string = id+"\t"+movie+"\t"+score1+"\t"+score2+"\n";
				if(f.contains(id)){
					out1.write(string);
				}
				if(m.contains(id)){
					out2.write(string);
				}
				line3 =  br3.readLine();
			}
			out1.flush();
			out2.flush();
			out1.close();
			out2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
