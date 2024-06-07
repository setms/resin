package com.remonsinnema.resin2modules.module;

import com.remonsinnema.resin2modules.graph.Vertex;

import java.util.Collection;


public record Module(String name, Collection<Vertex> contents) implements Vertex {

}
