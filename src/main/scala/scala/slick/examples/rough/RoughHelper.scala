package scala.slick.examples.rough

import scala.slick.examples.rough._

object RoughHelper {
	def myFilter(trip: DirectRough.TrainTrip) = {
		trip.countryID == 1
	}
}