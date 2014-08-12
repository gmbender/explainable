package com.github.explainable.example.demo;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 9/20/13 Time: 10:12 PM To change this template
 * use File | Settings | File Templates.
 */
public interface DemoRunner {
	void printEmptySection(String header);

	void printSection(String header, List<?> values);

	void load();
}
