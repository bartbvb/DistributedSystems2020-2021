package rental;

public interface ICarType {

	String getName();

	int getNbOfSeats();

	boolean isSmokingAllowed();

	double getRentalPricePerDay();

	float getTrunkSpace();

	/*************
	 * TO STRING *
	 *************/

	String toString();

	int hashCode();

	boolean equals(Object obj);

}