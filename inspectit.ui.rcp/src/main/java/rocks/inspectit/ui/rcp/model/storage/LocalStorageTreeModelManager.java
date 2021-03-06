package rocks.inspectit.ui.rcp.model.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import rocks.inspectit.shared.cs.storage.LocalStorageData;
import rocks.inspectit.shared.cs.storage.label.AbstractStorageLabel;
import rocks.inspectit.shared.cs.storage.label.type.AbstractStorageLabelType;
import rocks.inspectit.ui.rcp.formatter.ImageFormatter;
import rocks.inspectit.ui.rcp.formatter.TextFormatter;
import rocks.inspectit.ui.rcp.model.Composite;
import rocks.inspectit.ui.rcp.model.Leaf;

/**
 * Tree model manager for storage manager view that displays the local storage data.
 *
 * @author Ivan Senic
 *
 */
public class LocalStorageTreeModelManager {

	/**
	 * Collection of {@link LocalStorageData} to be displayed in tree.
	 */
	private Collection<LocalStorageData> localStorageDataCollection;

	/**
	 * Label type for grouping.
	 */
	private AbstractStorageLabelType<?> storageLabelType;

	/**
	 * Default constructor.
	 *
	 * @param localStorageDataCollection
	 *            Collection of {@link LocalStorageData} to be displayed in tree.
	 * @param storageLabelType
	 *            Label type for grouping.
	 */
	public LocalStorageTreeModelManager(Collection<LocalStorageData> localStorageDataCollection, AbstractStorageLabelType<?> storageLabelType) {
		super();
		this.localStorageDataCollection = localStorageDataCollection;
		this.storageLabelType = storageLabelType;
	}

	/**
	 * Returns objects divided either by the provided label class, or by
	 * {@link rocks.inspectit.ui.rcp.repository.CmrRepositoryDefinition} they are located to.
	 *
	 * @return Returns objects divided either by the provided label class, or by
	 *         {@link rocks.inspectit.ui.rcp.repository.CmrRepositoryDefinition} they are located
	 *         to.
	 */
	public Object[] getRootObjects() {
		if (CollectionUtils.isEmpty(localStorageDataCollection)) {
			return new Object[0];
		}

		if (null != storageLabelType) {
			Composite unknown = new Composite();
			unknown.setName("Unknown");
			unknown.setImage(ImageFormatter.getImageForLabel(storageLabelType));
			boolean addUnknown = false;
			Map<Object, Composite> map = new HashMap<>();
			for (LocalStorageData localStorageData : localStorageDataCollection) {
				List<? extends AbstractStorageLabel<?>> labelList = localStorageData.getLabels(storageLabelType);
				if (CollectionUtils.isNotEmpty(labelList)) {
					for (AbstractStorageLabel<?> label : labelList) {
						Composite c = map.get(TextFormatter.getLabelValue(label, true));
						if (c == null) {
							c = new Composite();
							c.setName(TextFormatter.getLabelName(label) + ": " + TextFormatter.getLabelValue(label, true));
							c.setImage(ImageFormatter.getImageForLabel(storageLabelType));
							map.put(TextFormatter.getLabelValue(label, true), c);
						}
						LocalStorageLeaf localStorageLeaf = new LocalStorageLeaf(localStorageData);
						localStorageLeaf.setParent(c);
						c.addChild(localStorageLeaf);
					}
				} else {
					unknown.addChild(new LocalStorageLeaf(localStorageData));
					addUnknown = true;
				}
			}
			ArrayList<Composite> returnList = new ArrayList<>();
			returnList.addAll(map.values());
			if (addUnknown) {
				returnList.add(unknown);
			}
			return returnList.toArray(new Composite[returnList.size()]);
		} else {
			List<Leaf> leafList = new ArrayList<>();
			for (LocalStorageData localStorageData : localStorageDataCollection) {
				leafList.add(new LocalStorageLeaf(localStorageData));
			}
			return leafList.toArray();
		}
	}

}
