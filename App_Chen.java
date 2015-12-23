package SampleRecommender.SampleRecommender;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.ClusterSimilarity;
import org.apache.mahout.cf.taste.impl.recommender.FarthestNeighborClusterSimilarity;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.TreeClusteringRecommender;
import org.apache.mahout.cf.taste.impl.recommender.knn.KnnItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.knn.NonNegativeQuadraticOptimizer;
import org.apache.mahout.cf.taste.impl.recommender.knn.Optimizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CachingUserSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.SpearmanCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.CosineSimilarity;

import com.sun.jersey.server.impl.model.method.dispatch.VoidVoidDispatchProvider;

import org.apache.mahout.cf.taste.eval.RecommenderBuilder;


/**
 * Hello world!
 *
 */
public class App 
{
	static String unknown = "Unknown Movie Name";
	public ArrayList<String> f = new ArrayList<String>();
	public ArrayList<String> m = new ArrayList<String>();
	public Hashtable<String, String> movie = new Hashtable<String, String>();
	public ArrayList<String> rm = new ArrayList<String>();
	public Map<Long,Integer> f_map = new TreeMap<Long, Integer>();
	public Map<Long, Integer> m_map = new TreeMap<Long, Integer>();
	public List<Map.Entry<Long,Integer>> f_map_sorted = null;
	public List<Map.Entry<Long,Integer>> m_map_sorted = null;
	public double i =0;
	
 	public App(){
 		String path0 = "/Users/yanjingchen/Desktop/bigdata-project/Dataset/ydata-user.txt";
 		String path1 = "/Users/yanjingchen/Desktop/bigdata-project/Dataset/ydata-moviemap.txt";
 		
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
		
		File filename1 = new File(path1);
		InputStreamReader reader1 = null;
		BufferedReader br1=null;
		try {
			reader1 = new InputStreamReader(new FileInputStream(filename1));
			br1 = new BufferedReader(reader1);
			String line1 = "";
			line1 = br1.readLine();
			while(line1 != null){
				line1 = line1.trim();
				String[] item1 = line1.split("\\t");
				String id = item1[0];
				String title = item1[1];
				movie.put(id, title);
				line1 = br1.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 	
 	public void ubRec(DataModel model) throws TasteException{
 		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
    	UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
    	UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
    	List<RecommendedItem> recommendations = recommender.recommend(1, 2);
    	for (RecommendedItem recommendation : recommendations) {
    	  System.out.println(recommendation);
    	}
 	}
 	
 	public void save(UserBasedRecommender recommender, int num, int type) throws NumberFormatException, TasteException, IOException{
 		
 		String path0 = "/Users/yanjingchen/Desktop/bigdata-project/Dataset/ydata-f-rec"+num+".txt";
 		String path1 = "/Users/yanjingchen/Desktop/bigdata-project/Dataset/ydata-m-rec"+num+".txt";
 		String path2 = "/Users/yanjingchen/Desktop/bigdata-project/Dataset/nodes.txt";
 		String path3 = "/Users/yanjingchen/Desktop/bigdata-project/Dataset/edges.txt";
 		
 		FileWriter n1 = new FileWriter(path2,true);
 		FileWriter e1 = new FileWriter(path3,true);
 		BufferedWriter out3=null;
 		BufferedWriter out4= null;
 		out3 = new BufferedWriter(n1);
 		out4 = new BufferedWriter(e1);
 		
 		
 		if(type == 1) {
 			FileWriter f1 = new FileWriter(path0,true);
 			BufferedWriter out1=null;
 			out1 = new BufferedWriter(f1);
 			for(String id : f){
 				out3.write(id+", f\n");
 				System.out.println("user: "+id);
 				List<RecommendedItem> recommendations = recommender.recommend(Integer.parseInt(id), num);
 				if(recommendations.size() > 0){
 					for (RecommendedItem recommendation : recommendations) {
 						if(!rm.contains(Long.toString(recommendation.getItemID()))){
 							rm.add(Long.toString(recommendation.getItemID()));
 						}
 						if(!f_map.containsKey(recommendation.getItemID())){
 							f_map.put(recommendation.getItemID(), 1);
 						}
 						else {
 							int value = f_map.get(recommendation.getItemID())+1;
 							f_map.put(recommendation.getItemID(), value);
 						}
 						String string = id +"\t"+recommendation.getItemID()+"\t"+recommendation.getValue()+"\n";
 						String string1= id+","+recommendation.getItemID()+"\n";
 						out1.write(string);
 						out4.write(string1);
 					}
 				}
 				else{
 					out1.write(id + "\tN\tN\n");
 				}
 			}
 			out1.flush();
 			out1.close();
 			out3.flush();
 			out4.flush();
 			out3.close();
 			out4.close();
 		}
 		else
 		{
 			FileWriter m1 = new FileWriter(path1,true);
 			BufferedWriter out2 = null;
 			out2 = new BufferedWriter(m1);
 			for(String id : m){
 				out3.write(id+", m\n");
 				System.out.println("user: "+id);
 				List<RecommendedItem> recommendations = recommender.recommend(Integer.parseInt(id), num);
 				if(recommendations.size() > 0){
 					for (RecommendedItem recommendation : recommendations) {
 						if(!rm.contains(Long.toString(recommendation.getItemID()))){
 							rm.add(Long.toString(recommendation.getItemID()));
 						}
 						if(!m_map.containsKey(recommendation.getItemID())){
 							m_map.put(recommendation.getItemID(), 1);
 						}
 						else {
 							int value = m_map.get(recommendation.getItemID())+1;
 							m_map.put(recommendation.getItemID(), value);
 						}
 						String string = id +"\t"+recommendation.getItemID()+"\t"+recommendation.getValue()+"\n";
 						String string1= id+","+recommendation.getItemID()+"\n";
 						out4.write(string1);
 						out2.write(string);
 					}
 				}
 				else{
 					out2.write(id + "\tN\tN\n");
 				}
 			}
 			out3.flush();
 			out3.close();
 			out4.flush();
 			out4.close();
 			out2.flush();
 			out2.close();
 		}
 		
		System.out.println("Saved successfully");
 	}
 	
 	public void getTopMovie(){
 		f_map_sorted = new ArrayList<Map.Entry<Long,Integer>>(f_map.entrySet());
 		m_map_sorted = new ArrayList<Map.Entry<Long,Integer>>(m_map.entrySet());
 		float f_total = 0;
 		float m_total = 0;
 		for(long e: f_map.keySet()){
 			f_total += f_map.get(e);
 		}
 		for(long e: m_map.keySet()){
 			m_total += m_map.get(e);
 		}
 		
 		 Collections.sort(f_map_sorted,new Comparator<Map.Entry<Long,Integer>>() {
             public int compare(Entry<Long, Integer> o1, Entry<Long, Integer> o2) {
                 return o2.getValue().compareTo(o1.getValue());
             } 
         });
 		Collections.sort(m_map_sorted,new Comparator<Map.Entry<Long,Integer>>() {
            public int compare(Entry<Long, Integer> o1, Entry<Long, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            } 
        });
 		
 		System.out.println("Top 10 movies that are recommended in famale group: ");
 		int cnt = 0;
 		for(Entry<Long, Integer> mapping:f_map_sorted){
 			cnt ++;
 			long id = mapping.getKey();
 			String string ="";
 			if(movie.get(Long.toString(id))==null) string = "Unknown Movie Name";
 			else string = movie.get(Long.toString(id));
 			System.out.println(id+", "+mapping.getValue()/f_total);
// 			System.out.println("Rank "+cnt +": "+string+ ", ratio: "+ mapping.getValue()/f_total);
 			if(cnt == 10) break;
 		}
 		
 		System.out.println("Top 10 movies that are recommended in male group: ");
 		cnt = 0;
 		for(Entry<Long, Integer> mapping:m_map_sorted){
 			cnt ++;
 			long id = mapping.getKey();
 			String string ="";
 			if(movie.get(Long.toString(id))==null) string = "Unknown Movie Name";
 			else string = movie.get(Long.toString(id));
 			System.out.println(id+", "+ mapping.getValue()/m_total);
// 			System.out.println("Rank "+cnt +": "+string+ ", ratio: "+ mapping.getValue()/m_total);
 			if(cnt == 10) break;
 		}
 		
 	}
 	
 	public double evaluate(DataModel model) throws TasteException{
 		RecommenderEvaluator evaluator =new AverageAbsoluteDifferenceRecommenderEvaluator();
// 		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
 		double score = 0;
 		long start = System.currentTimeMillis();
 		for (int t = 0;t<100;++t){
// 			UserNeighborhood neighborhood = new ThresholdUserNeighborhood(i, similarity, model);
 			RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
 				public Recommender buildRecommender(DataModel model) throws TasteException {
 				UserSimilarity similarity = new EuclideanDistanceSimilarity(model);
// 				UserSimilarity similarity = new CachingUserSimilarity(new SpearmanCorrelationSimilarity(model), model);;
// 				UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
// 				UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
 			    UserNeighborhood neighborhood =new ThresholdUserNeighborhood(0.9, similarity, model);
// 				return new GenericUserBasedRecommender(model, neighborhood, similarity);
 				
 				ItemSimilarity similarity1 = new EuclideanDistanceSimilarity(model);
 		 		return new GenericItemBasedRecommender(model, similarity1);
 				}
 			};
 			score += evaluator.evaluate(recommenderBuilder, null, model, 0.90, 0.10);
 		}
 		
 		long stop = System.currentTimeMillis();
 		System.out.println("score: " + score/100);
 		System.out.println(stop-start);
 		System.out.println("average processing time: "+ (stop-start)/1000+"s. ");
 		return score/100;
 	}
 	
 	//item based recommender
 	public Recommender itemRecommender(DataModel model)throws TasteException {
 		ItemSimilarity similarity = new EuclideanDistanceSimilarity(model);
 		return new GenericItemBasedRecommender(model, similarity);
 	}
	
    public static void main( String[] args ) throws IOException, TasteException
    {
//        System.out.println( "Hello World!" )
    	
    	///Users/yanjingchen/Desktop/bigdata-project/Dataset/ydata-f.txt
    	///Users/yanjingchen/Desktop/bigdata-project/Dataset/ydata-m.txt
    	///Users/yanjingchen/Downloads/data.csv
    	App my_app = new App();
    	int num = 5;//Recommended Movie for each user

    	//user-based recommendation
    	File file0 = new File("/Users/yanjingchen/Desktop/bigdata-project/Dataset/ydata-f.txt");
    	DataModel model = new FileDataModel(file0);
    	
    	//Evaluation to determine the best parameters
//    	System.out.println("Fmale group: ");
//    	my_app.evaluate(model);
    	
    	//final recommendation
    	UserSimilarity similarity = new EuclideanDistanceSimilarity(model);
    	UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
    	UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
    	
    	File file1 = new File("/Users/yanjingchen/Desktop/bigdata-project/Dataset/ydata-m.txt");
    	DataModel model1 = new FileDataModel(file1);
    	
    	//evaluation to determine the best parameter
//    	System.out.println("Male group: ");
//    	my_app.evaluate(model1);
    	
    	//final recommendation
    	UserSimilarity similarity1 = new EuclideanDistanceSimilarity(model1);
    	UserNeighborhood neighborhood1 = new NearestNUserNeighborhood(2, similarity1, model1);
    	UserBasedRecommender recommender1 = new GenericUserBasedRecommender(model1, neighborhood1, similarity1);
    	//user-based Recommender
    	
//    	for(my_app.i = 0.1;my_app.i<1.0;my_app.i+=0.1){
//    		paras.add(my_app.evaluate(model));
//    	}
//    	for(double value: paras){
//    		System.out.println(value);
//    	}
//    	double max_score = 0;
//    	double th = 0.1;
//    	System.out.println(paras.size());
//    	for(int i = 0;i<paras.size();++i){
//    		if(max_score < paras.get(i)){
//    			max_score = paras.get(i);
//    			th = i*0.1;
//    		}
//    	}
//    	System.out.println("Th: "+ th);
    	
    	
//    	//itembased recommender
//    	Recommender recommender2 = my_app.itemRecommender(model);
//    	
//    	//SVD recommender
//    	Recommender recommender3 = new SVDRecommender(model, new ALSWRFactorizer(model, 10, 0.05, 10));
//    	
//    	//knn recommender
//    	ItemSimilarity similarity1 = new LogLikelihoodSimilarity(model);
//    	Optimizer optimizer = new NonNegativeQuadraticOptimizer();
//    	Recommender recommender4 = new KnnItemBasedRecommender(model, similarity1, optimizer, 10);
//    	
//    	//cluster-based recommender
//    	UserSimilarity similarity2 = new LogLikelihoodSimilarity(model);
//    	ClusterSimilarity clusterSimilarity =new FarthestNeighborClusterSimilarity(similarity2);
//    	Recommender recommender5 = new TreeClusteringRecommender(model, clusterSimilarity, 10);
    	
    	//Output the recommend result for each user
//    	System.out.println("Start to recommand "+num+" movies to users");
//    	for(String id : my_app.f){
//    		List<RecommendedItem> recommendations = recommender.recommend(Integer.parseInt(id), num);
//    		if(recommendations.size() > 0){
//    			System.out.print("Recommend Movies to user "+id+" : ");
//    			for (RecommendedItem recommendation : recommendations) {
//    	        	  long movie_id = recommendation.getItemID();
//    	        	  String movie_title = my_app.movie.get(Long.toString(movie_id));
//    	        	  double value = 0;
//    	        	  value = recommendation.getValue();
////    	        	  value = recommender3.estimatePreference(Long.parseLong(id), movie_id)/70*13;
//    	        	  if(movie_title != null){
//    	        		  System.out.print("Title: "+ movie_title +", Estimated rating: "+value+", ");
//    	        	  }
//    	        	  else System.out.print("Title: "+ my_app.unknown +", Estimated rating: "+value+", ");
//    	        }
//    		}
//    		else{
//    			System.out.print("No recommendation to user: "+id);
//    		}
//    		System.out.println();
//    	}
    	
    	
    	
    	//Save the result locally
    	my_app.save(recommender, num, 1);
    	my_app.save(recommender1, num, 2);
    	
    	//Recommend 10 movies to each group
    	my_app.getTopMovie();
//    	
//    	save all the recommended movies
    	FileWriter f3 = new FileWriter("/Users/yanjingchen/Desktop/bigdata-project/Dataset/nodes.txt",true);
    	BufferedWriter out3 = new BufferedWriter(f3);
    	for(String node: my_app.rm){
    		out3.write(node+", movie\n");
    	}
    	out3.flush();
    	out3.close();
    	System.out.println("The whole process is over");
    	
    }
}
