package org.autoflowchart.utils;

import org.autoflowchart.objects.Arrow;
import org.autoflowchart.objects.Shape;

import java.util.*;

public class WidthMap
{
	private Map<Integer, Integer> map = new TreeMap<>();

	public WidthMap ()
	{
		this.map.put(0, 0);
	}

	public WidthMap (Arrow arrow)
	{
		if (arrow.xPoints.size() > 0) {
			int i;
			for (i = 0; i < arrow.xPoints.size() - 1; i++) {
				int x = arrow.xPoints.get(i);
				int y = arrow.yPoints.get(i);
				this.addPoint(y, x);
			}
			int y = arrow.yPoints.get(i);
			this.addPoint(y, 0);
		} else
			this.map.put(0, 0);
	}

	public WidthMap (Map<Integer, Integer> map)
	{
		this.map = new TreeMap<>(map);
	}

	/*public void addShape (Shape shape)
	{
		int startPoint = shape.y;
		int endPoint = shape.y + shape.height;
		int rightBorder = shape.x + shape.width;

		int width = this.getWidth(startPoint);
		if (rightBorder > width)
			this.addPoint(startPoint, rightBorder);
		Integer prevPoint = this.findPoint(startPoint);
		Integer point = this.findNextPoint(prevPoint);
		while (point != null && point <= endPoint)
		{
			width = this.getWidth(point);
			if (rightBorder > width) {
				if (rightBorder != this.getWidth(prevPoint))
					this.addPoint(point, rightBorder);
				else
					this.map.remove(point);
			}
			prevPoint = point;
			point = this.findNextPoint(prevPoint);
		}
	}*/

	public void addShape (Shape shape)
	{
		int startPoint = shape.y;
		int endPoint = shape.y + shape.height;
		int rightBorder = shape.x + shape.width;
		Integer prevPoint = this.findPreviousPoint(startPoint);
		Integer point = startPoint;

		Integer width = null;

		while (point != null && point <= endPoint)
		{
			width = this.getWidth(point);

			if (rightBorder > width) {
				if (rightBorder != this.getWidth(prevPoint))
					this.addPoint(point, rightBorder);
				else
					this.removePoint(point);
			} else if (width == this.getWidth(prevPoint) && !prevPoint.equals(point))
				this.removePoint(point);

			prevPoint = point;
			point = this.findNextPoint(prevPoint);
		}

		if (!this.hasPoint(endPoint))
			if (width != null)
				if (width != this.getWidth(prevPoint))
					this.addPoint(endPoint, width);
	}

	public void addArrow (Arrow arrow)
	{
		WidthMap arrowMap = new WidthMap(arrow);
		Set<Integer> arrowPoints = arrowMap.getPoints();
		Integer startPoint = arrowMap.getFirstPoint();
		Integer endPoint = arrowMap.getLastPoint();
		Set<Integer> points = this.getPoints(startPoint, endPoint);
		points.addAll(arrowPoints);
		List<Integer> pointsList = new ArrayList<>(points);

		Integer oldWidth = null;
		Integer prevPoint = this.findPreviousPoint(startPoint);

		/*for (Integer point : points)
		{
			oldWidth = this.getWidth(point);
			newWidth = arrowMap.getWidth(point);

			if (newWidth > oldWidth) {
				newLine = false;
				if (newWidth != this.getWidth(prevPoint))
					this.addPoint(point, newWidth);
				else
					this.removePoint(point);
			} else if ((newLine && this.hasPoint(point)) || (!newLine && arrowMap.hasPoint(point))) {
				this.addPoint(point, oldWidth);
			} else if (oldWidth == this.getWidth(prevPoint) && !prevPoint.equals(point))
				this.removePoint(point);
			else
				newLine = true;

			prevPoint = point;
		}*/

		int lastNativeWidth = 0;
		boolean onNewLine = false;

		for (Integer point : points) {
			if (this.hasPoint(point))
				lastNativeWidth = this.getWidth(point);

			int curWidth, newWidth;
			if (onNewLine) {
				curWidth = arrowMap.getWidth(point);
				newWidth = lastNativeWidth;
			} else {
				curWidth = this.getWidth(point);
				newWidth = arrowMap.getWidth(point);
			}

			if (newWidth > curWidth) {
				if (onNewLine) {
					if (this.getWidth(point) != lastNativeWidth)
						this.addPoint(point, lastNativeWidth);
					else if (newWidth == this.getWidth(this.findPreviousPoint(point)))
						this.removePoint(point);
				} else {
					curWidth = newWidth;
				}
				onNewLine = !onNewLine;
			}

			if (onNewLine) {
				if (arrowMap.hasPoint(point) && this.getWidth(this.findPreviousPoint(point)) != curWidth) {
					this.addPoint(point, curWidth);
				} else
					this.removePoint(point);
			}
		}
	}

	private void addPoint (int y, int width)
	{
		this.map.put(y, width);
	}

	private void removePoint (int y)
	{
		this.map.remove(y);
	}

	private boolean hasPoint (int y)
	{
		return this.map.containsKey(y);
	}

	private Integer findPoint (int y)
	{
		if (this.map.containsKey(y))
			return y;
		else {
			Set<Integer> keySet = this.map.keySet();
			Integer prev = 0;
			for (Integer key : keySet) {
				if (key > y)
					return prev;
				prev = key;
			}
			return prev;
		}
	}

	private Integer findPreviousPoint (int y)
	{
		Set<Integer> keySet = this.map.keySet();
		Integer prev = 0;
		for (Integer key : keySet) {
			if (key >= y)
				return prev;
			prev = key;
		}
		return prev;
	}

	private Integer findNextPoint (int y)
	{
		Set<Integer> keySet = this.map.keySet();
		List<Integer> keyList = new ArrayList<>(keySet);
		Integer key = this.findPoint(y);
		int index = keyList.indexOf(key);

		if (index + 1 < keyList.size())
			return keyList.get(index + 1);
		else
			return null;
	}

	private Integer getFirstPoint ()
	{
		Set<Integer> keySet = this.map.keySet();
		List<Integer> keyList = new ArrayList<>(keySet);
		return keyList.get(0);
	}

	private Integer getLastPoint ()
	{
		Set<Integer> keySet = this.map.keySet();
		List<Integer> keyList = new ArrayList<>(keySet);
		return keyList.get(keyList.size() - 1);
	}

	private Set<Integer> getPoints ()
	{
		return this.map.keySet();
	}

	private Set<Integer> getPoints (int from, int to)
	{
		Set<Integer> keySet = this.map.keySet();
		Set<Integer> chosenKeySet = new TreeSet<>();
		for (Integer key : keySet)
		{
			if (key > to)
				break;
			if (key >= from)
				chosenKeySet.add(key);
		}
		return chosenKeySet;
	}

	private int getWidth (int y)
	{
		Integer key = this.findPoint(y);
		return this.map.get(key);
	}

	public Map<Integer, Integer> getMap ()
	{
		return this.map;
	}

	public int findMaxWidth (int from, int to)
	{
		Set<Integer> points = this.getPoints(from, to);
		int maxWidth = 0;
		for (Integer point : points) {
			int width = this.getWidth(point);
			if (width > maxWidth)
				maxWidth = width;
		}
		return maxWidth;
	}

	public int findMinWidth (int from, int to)
	{
		Set<Integer> points = this.getPoints(from, to);
		int minWidth = Integer.MAX_VALUE;
		for (Integer point : points) {
			int width = this.getWidth(point);
			if (width < minWidth)
				minWidth = width;
		}
		return minWidth;
	}
}
