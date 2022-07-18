package com.iurysouza.challenge.ui

import java.lang.Math.toRadians
import org.jgrapht.DirectedGraph
import org.jgrapht.Graph
import org.jgrapht.graph.SimpleDirectedGraph
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class DistanceCalculatorImpl : DistanceCalculator {
    /**
     * Calculate distance between two points in latitude and longitude.
     *  Uses Haversine method as its base.
     *
     * @returns Distance in Km
     */
    override fun distance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
    ): Double {
        val R = 6371 // Radius of the earth
        val latDistance = toRadians(lat2 - lat1)
        val lonDistance = toRadians(lon2 - lon1)
        val a = (sin(latDistance / 2) * sin(latDistance / 2)
                + (cos(toRadians(lat1)) * cos(toRadians(lat2))
                * sin(lonDistance / 2) * sin(lonDistance / 2)))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        var distance = R * c * 1000 // convert to meters
        distance = distance.pow(2.0)
        val result = sqrt(distance)
        // convert result to kilometers
        return result / 1000
    }

    override fun travelingSalesman(input: List<Triple<String, String, Double>>): List<Edge>? {
        val graph = SimpleDirectedGraph<String, Edge>(Edge::class.java)

        input.forEach { current ->
            val (source, target, distance) = current
            graph += source
            graph += target
            graph += Edge(source, target, distance.toInt())
            graph += Edge(target, source, distance.toInt())
        }

        return graph.findTravellingSalesmanRoute()
    }

    /**
     * A class defining an Edge in the Graph.
     */
    data class Edge(val source: String, val target: String, val distance: Int)


    operator fun <V> Graph<V, *>.plusAssign(vertex: V) {
        addVertex(vertex)
    }

    operator fun Graph<String, Edge>.plusAssign(edge: Edge) {
        addEdge(edge.source, edge.target, edge)
    }


    /**
     * An extension function for DirectedGraphs: It calculates the Travelling Salesman Route via recursive bruteforce.
     */
    private fun DirectedGraph<String, Edge>.findTravellingSalesmanRoute(route: List<Edge>? = null): List<Edge>? {
        val routes = if (route == null) {
            // If no route is given, this is the first call.
            // For every Edge in the Graph, we try out a Route that starts with it
            edgeSet().map { findTravellingSalesmanRoute(listOf(it)) }
        } else if (route.visited().toSet() == vertexSet()) {
            // If the Route contains all vertices from the Graph, we found a solution
            listOf(route)
        } else {
            // [route] contains a partial, unfinished route.
            // First, search for all outgoing edges of the current position of the route
            outgoingEdgesOf(route.last().target)
                // Then, remove all Edges whose targets already were visited
                .filterNot { route.visited().contains(it.target) }
                // Finally, append the new Edge to the route and go on recursively
                .map { findTravellingSalesmanRoute(route + it) }
        }

        // Filter all invalid routes and return the best route - the one with minimum distance
        return routes.filterNotNull().minByOrNull { it.distance() }
    }
}

interface DistanceCalculator {
    fun distance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
    ): Double

    /** Calculates the Travelling Salesman Route via recursive bruteforce. */
    fun travelingSalesman(input: List<Triple<String, String, Double>>): List<DistanceCalculatorImpl.Edge>?
}

fun Iterable<DistanceCalculatorImpl.Edge>.distance() = sumBy { it.distance }

fun Iterable<DistanceCalculatorImpl.Edge>.visited() = map { it.source } + last().target
