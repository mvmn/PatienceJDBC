package x.mvmn.util.collections;

import java.util.ArrayList;
import java.util.List;

public class CollectionsHelper {

	public interface Checker<T> {
		public boolean check(T val);
	}

	public static <T> List<T> createFilteredList(List<T> list, Checker<T> checker) {
		List<T> result = null;

		if (list != null) {
			result = new ArrayList<T>();
			if (checker != null) {
				for (T item : list) {
					if (checker.check(item)) {
						result.add(item);
					}
				}
			}
		}

		return result;
	}

}
