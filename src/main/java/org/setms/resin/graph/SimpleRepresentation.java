package org.setms.resin.graph;

/**
 * Simple representation of a graph as a multi-line string.
 */
public class SimpleRepresentation implements Representation {

    @Override
    public String apply(Graph graph) {
        var result = new StringBuilder();
        result.append("graph {\n");
        graph.vertices()
                .map(Vertex::name)
                .sorted()
                .map("    %s\n"::formatted)
                .forEach(result::append);
        graph.edges().map(Edge::toString)
                .sorted()
                .map("    %s\n"::formatted)
                .forEach(result::append);
        result.append("}");
        return result.toString();
    }
}
