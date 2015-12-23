package com.prediction.RecommendAPP;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class Recommender 
{
    public static void main( String[] args ) throws IOException, TasteException
    {
    	DataModel model = new FileDataModel(new File("/Users/Patrick/Downloads/BigData/yahoo_movie/ydata-ymovies-user-movie-ratings-train-v1_0.txt"));
    	UserSimilarity similarity = new EuclideanDistanceSimilarity(model);
    	//UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
    	//UserBasedRecommender recommender = new GenericUserBasedRecommender(model,neighborhood,similarity);
    	
    	UserNeighborhood neighborhood =new NearestNUserNeighborhood (100, similarity, model);
    	GenericUserBasedRecommender recommender =new GenericUserBasedRecommender(model, neighborhood, similarity);
    	
    	String filename="/Users/Patrick/Downloads/BigData/recommendbetter.txt";
    	PrintWriter outputStream =new PrintWriter(filename);
    	for (int i=1;i<7643;i++){
    		List<RecommendedItem> recommendations = recommender.recommend(i, 5);
    		for (RecommendedItem recommendation : recommendations){

    			outputStream.print(i+"\t");
    			outputStream.print(recommendation.getItemID()+"\t");
    			outputStream.println(recommendation.getValue());
    			outputStream.flush();

    			System.out.print(i+"\t");
    			System.out.println( recommendation );
    		}	
    	}
    	outputStream.close();
    	
    }
}
