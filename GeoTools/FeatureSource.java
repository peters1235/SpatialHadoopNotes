/*
This class provides a high-level API for operations on feature data. 
Typically, when working with a data source such as a shapefile or database table 
you will initially create a DataStore object to connect to the physical source 
and then retrieve a FeatureSource to work with the feature data, 
as in this excerpt from the GeoTools Quickstart example (http://geotools.org/quickstart.html) 


     File file = ...
     FileDataStore store = FileDataStoreFinder.getDataStore(file);
     FeatureSource featureSource = store.getFeatureSource();
    只读
*/

interface FeatureSource<T extends FeatureType, F extends Feature>

	FeatureCollection<T, F> getFeatures(Filter filter) throws IOException;


	/*
	Retrieves all features in the form of a FeatureCollection. 

	The following statements are equivalent: 


	     featureSource.getFeatures();
	     featureSource.getFeatures(Filter.INCLUDE);
	     featureSource.getFeatures(Query.ALL);
	*/
	FeatureCollection<T, F> org.geotools.data.FeatureSource.getFeatures() throws IOException

