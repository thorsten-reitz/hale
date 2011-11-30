/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;

import com.google.common.base.Preconditions;
import com.google.common.io.InputSupplier;

import de.cs3d.util.eclipse.extension.ExtensionObjectFactoryCollection;
import de.cs3d.util.eclipse.extension.FactoryFilter;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderExtension;


/**
 * Hale I/O utilities
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public abstract class HaleIO {
	
	private static final ALogger log = ALoggerFactory.getLogger(HaleIO.class);
	
	/**
	 * Filter I/O provider factories by content type
	 * @param factories the I/O provider factories
	 * @param contentType the content type factories must support
	 * @return provider factories that support the given content type 
	 */
	public static List<IOProviderDescriptor> filterFactories(
			Collection<IOProviderDescriptor> factories, IContentType contentType) {
		List<IOProviderDescriptor> result = new ArrayList<IOProviderDescriptor>();
		
		for (IOProviderDescriptor factory : factories) {
			Set<IContentType> supportedTypes = factory.getSupportedTypes();
			
			// check if contentType is supported
			for (IContentType test : supportedTypes) {
				if (isCompatibleContentType(test, contentType)) {
					result.add(factory);
					break;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Find the content types that match the given file name and/or input.
	 * 
	 * NOTE: The implementation should try to restrict the result to one 
	 * content type and only use the input supplier if absolutely needed.
	 * 
	 * @param types the types to match 
	 * @param in the input supplier to use for testing, may be <code>null</code>
	 *   if the file name is not <code>null</code>
	 * @param filename the file name, may be <code>null</code> if the input
	 *   supplier is not <code>null</code>
	 * @return the matched content types
	 */
	public static List<IContentType> findContentTypesFor(Collection<IContentType> types, 
			InputSupplier<? extends InputStream> in, String filename) {
		Preconditions.checkArgument(filename != null || in != null, 
				"At least one of input supplier and file name must not be null");
		
		List<IContentType> results = new ArrayList<IContentType>();
		
		if (filename != null) {
			// test file extension
			for (IContentType type : types) {
				String ext = FilenameUtils.getExtension(filename);
				if (ext != null && !ext.isEmpty()) {
					String[] extensions = type.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
					boolean match = false;
					for (int i = 0; i < extensions.length && !match; i++) {
						if (extensions[i].equalsIgnoreCase(ext)) {
							match = true;
						}
					}
					if (match) {
						results.add(type);
					}
				}
			}
		}
		
		if ((results.isEmpty() || results.size() > 1) && in != null) {
			IContentTypeManager ctm = Platform.getContentTypeManager();
			try {
				InputStream is = in.getInput();
				IContentType[] candidates = ctm.findContentTypesFor(is, filename);
				
				for (IContentType candidate : candidates) {
					if (types.contains(candidate)) {
						results.add(candidate);
					}
				}
				
				is.close();
			} catch (IOException e) {
				log.warn("Could not read input to determine content type", e);
			}
			
			// only use the testers if
			// - we have no results from the filename match
			// - we have more than one result from the filename match (as we might have to restrict the result)
			// - the input supplier is set
			
			// build a map to commit to DependencyOrderedList
//			Map<ContentType, Set<ContentType>> map = new HashMap<ContentType, Set<ContentType>>();
//			
//			for (ContentType type : types){
//				BundleContentType bct = getBundleContentType(type);
//				if (bct != null){
//					Set<ContentType> set = new HashSet<ContentType>();
//					String father = bct.getContentType().getParent();
//					if (father != null){
//						set.add(ContentType.getContentType(father));
//						map.put(type, set);
//					}
//					else {
//						map.put(type, null);
//					}
//				}
//			}
//			
//			// order the given content types
//			DependencyOrderedList<ContentType> orderedlist = new DependencyOrderedList<ContentType>(map);
//			List<ContentType> list = orderedlist.getInternalList();
//			
//			// last content type has to check first (has the most dependencies)
//			for (int i = list.size() - 1; i >= 0; i--){
//				ContentType cont = list.get(i);
//				BundleContentType bct = getBundleContentType(cont);
//				ContentTypeTester tester = bct.getTester();
//				if (tester != null) {
//					try {
//						InputStream is = in.getInput();
//						try {
//							if (tester.matchesContentType(is)) {
//								results.add(cont);
//								return results;
//							}
//
//						} finally {
//							try {
//								is.close();
//							} catch (IOException e) {
//								// ignore
//							}
//						}
//					} catch (IOException e) {
//						log.warn("Could not open input stream for testing the content type, tester for content type {0} is ignored", cont);
//					}
//				}
//			}
		}
		
		return results;
	}
	
	/**
	 * Get the I/O provider factories of a certain type
	 *
	 * @param providerType the provider type, usually an interface
	 * @return the factories currently registered in the system
	 */
	public static <P extends IOProvider> Collection<IOProviderDescriptor> 
			getProviderFactories(final Class<P> providerType) {
		return IOProviderExtension.getInstance().getFactories(new FactoryFilter<IOProvider, IOProviderDescriptor>() {
			
			@Override
			public boolean acceptFactory(IOProviderDescriptor descriptor) {
				return providerType.isAssignableFrom(descriptor.getProviderType());
			}
			
			@Override
			public boolean acceptCollection(
					ExtensionObjectFactoryCollection<IOProvider, IOProviderDescriptor> collection) {
				return true;
			}
		});
	}
	
	/**
	 * Find an I/O provider factory
	 * @param <P> the provider interface type
	 * 
	 * @param providerType the provider type, usually an interface
	 * @param contentType the content type the provider must match, may be 
	 *   <code>null</code> if providerId is set
	 * @param providerId the id of the provider to use, may be <code>null</code>
	 *   if contentType is set
	 * @return the I/O provider factory or <code>null</code> if no matching 
	 *   I/O provider factory is found
	 */
	public static <P extends IOProvider> IOProviderDescriptor findIOProviderFactory(
			Class<P> providerType, IContentType contentType, String providerId) {
		Preconditions.checkArgument(contentType != null || providerId != null);
		
		Collection<IOProviderDescriptor> factories = getProviderFactories(providerType);
		if (contentType != null) {
			factories = filterFactories(factories, contentType);
		}
		
		IOProviderDescriptor result = null;
		
		if (providerId != null) {
			for (IOProviderDescriptor factory : factories) {
				if (factory.getIdentifier().equals(providerId)) {
					result = factory;
					break;
				}
			}
		}
		else {
			//TODO choose priority based?
			if (!factories.isEmpty()) {
				result = factories.iterator().next();
			}
		}
		
		return result;
	}
	
	/**
	 * Creates an I/O provider instance
	 * @param <P> the provider interface type
	 * 
	 * @param providerType the provider type, usually an interface
	 * @param contentType the content type the provider must match, may be 
	 *   <code>null</code> if providerId is set
	 * @param providerId the id of the provider to use, may be <code>null</code>
	 *   if contentType is set
	 * @return the I/O provider preconfigured with the content type if it was 
	 *   given or <code>null</code> if no matching I/O provider is found
	 */
	@SuppressWarnings("unchecked")
	public static <P extends IOProvider> P createIOProvider(
			Class<P> providerType, IContentType contentType, String providerId) {
		IOProviderDescriptor factory = findIOProviderFactory(providerType, contentType, providerId);
		P result;
		try {
			result = (P) ((factory == null)?(null):(factory.createExtensionObject()));
		} catch (Exception e) {
			throw new RuntimeException("Could not create I/O provider", e);
		}
		
		if (result != null && contentType != null) {
			result.setContentType(contentType);
		}
		
		return result;
	}
	
	/**
	 * Find the content type for the given input
	 * @param <P> the provider interface type
	 * 
	 * @param providerType the provider type, usually an interface
	 * @param in the input supplier to use for testing, may be <code>null</code>
	 *   if the file name is not <code>null</code>
	 * @param filename the file name, may be <code>null</code> if the input
	 *   supplier is not <code>null</code>
	 * @return the content type or <code>null</code> if no matching content type 
	 *   is found
	 */
	public static <P extends IOProvider> IContentType findContentType(
			Class<P> providerType, InputSupplier<? extends InputStream> in, String filename) {
		Collection<IOProviderDescriptor> providers = getProviderFactories(providerType);
		
		// collect supported content types
		Set<IContentType> supportedTypes = new HashSet<IContentType>();
		for (IOProviderDescriptor factory : providers) {
			supportedTypes.addAll(factory.getSupportedTypes());
		}
		
		// find matching content type
		List<IContentType> types = findContentTypesFor(supportedTypes, in, filename);
		
		if (types == null || types.isEmpty()) {
			return null;
		}
		
		//TODO choose?
		return types.iterator().next();
	}
	
	/**
	 * Find an I/O provider instance for the given input
	 * @param <P> the provider interface type
	 * 
	 * @param providerType the provider type, usually an interface
	 * @param in the input supplier to use for testing, may be <code>null</code>
	 *   if the file name is not <code>null</code>
	 * @param filename the file name, may be <code>null</code> if the input
	 *   supplier is not <code>null</code>
	 * @return the I/O provider or <code>null</code> if no matching I/O provider 
	 *   is found
	 */
	public static <P extends IOProvider> P findIOProvider(
			Class<P> providerType, InputSupplier<? extends InputStream> in, String filename) {
		IContentType contentType = findContentType(providerType, in, filename);
		if (contentType == null) {
			return null;
		}
		
		return HaleIO.createIOProvider(providerType, contentType, null);
	}
	
	/**
	 * Test if the given value content type is compatible with the given 
	 * parent content type
	 * 
	 * @param parentType the parent content type
	 * @param valueType the value content type
	 * @return if the value content type is compatible with the parent content 
	 *   type
	 */
	public static boolean isCompatibleContentType(IContentType parentType,
			IContentType valueType) {
		return valueType.isKindOf(parentType);
	}

//	/**
//	 * Get the file extensions for the given content type
//	 * 
//	 * @param contentType the content type
//	 * @param prefix the prefix to add before the extensions, e.g. "." or "*.",
//	 *   may be <code>null</code>
//	 * @return the file extensions or <code>null</code>
//	 */
//	public static String[] getFileExtensions(ContentType contentType,
//			String prefix) {
//		SortedSet<String> exts = new TreeSet<String>();
//		
//		ContentTypeService cts = OsgiUtils.getService(ContentTypeService.class);
//		String[] typeExts = cts.getFileExtensions(contentType);
//		if (typeExts != null) {
//			for (String typeExt : typeExts) {
//				if (prefix == null) {
//					exts.add(typeExt);
//				}
//				else {
//					exts.add(prefix + typeExt);
//				}
//			}
//		}
//		
//		if (exts.isEmpty()) {
//			return null;
//		}
//		else {
//			return exts.toArray(new String[exts.size()]);
//		}
//	}

//	/**
//	 * Get all file extensions for the given content types
//	 * 
//	 * @param contentTypes the content types
//	 * @param prefix the prefix to add before the extensions, e.g. "." or "*.",
//	 *   may be <code>null</code>
//	 * @return the file extensions or <code>null</code>
//	 */
//	public static String[] getFileExtensions(Iterable<ContentType> contentTypes,
//			String prefix) {
//		SortedSet<String> exts = new TreeSet<String>();
//		
//		ContentTypeService cts = OsgiUtils.getService(ContentTypeService.class);
//		for (ContentType contentType : contentTypes) {
//			String[] typeExts = cts.getFileExtensions(contentType);
//			if (typeExts != null) {
//				for (String typeExt : typeExts) {
//					if (prefix == null) {
//						exts.add(typeExt);
//					}
//					else {
//						exts.add(prefix + typeExt);
//					}
//				}
//			}
//		}
//		
//		if (exts.isEmpty()) {
//			return null;
//		}
//		else {
//			return exts.toArray(new String[exts.size()]);
//		}
//	}
	
}