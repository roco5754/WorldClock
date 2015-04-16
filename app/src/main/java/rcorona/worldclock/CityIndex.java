package rcorona.worldclock;

import java.util.ArrayList;

/**
 * class to define the city idex. This is filled from the .csv of information about cities to grab
 * the information needed to pass into the user's data base of cities
 */
public class CityIndex {

    public String cityName;
    public int gmtOffset;
    public String charCode;

    public CityIndex(){}

    public CityIndex(String newCityName, int newOffset, String newCode){
        cityName = newCityName;
        gmtOffset = newOffset;
        charCode = newCode;

    }



//This overrides the structure to return one string of data rather than individual elements
    //by no means an elegant solution and affects the user interface display of search results
    //but I was stuck on this for awhile and chose to find a solution that "worked" in order to move
    //on to more important functionality
@Override
public String toString() {
    return this.cityName + "." + this.gmtOffset + "." + this.charCode;
}
}