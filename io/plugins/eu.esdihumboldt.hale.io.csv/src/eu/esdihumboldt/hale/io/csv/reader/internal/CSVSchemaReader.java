/*
 * HUMBOLDT: A Framework for Data Harmonization and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.io.csv.reader.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.namespace.QName;

import au.com.bytecode.opencsv.CSVReader;
import eu.esdihumboldt.hale.common.core.io.ContentType;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.io.impl.AbstractSchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.io.csv.CSVFileIO;

/**
 * Reads a schema from a CSV file.
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 */
public class CSVSchemaReader extends AbstractSchemaReader {

	/**
	 * Name of the parameter specifying the type name
	 */
	public static String PARAM_TYPENAME = "typename";
	
	public static String PARAM_SEPARATOR = "separator";
	
	public static String PARAM_QUOTE = "quote";
	
	public static String PARAM_ESCAPE = "escape";

	/**
	 * The separating sign for the CSV file to be read (can be '\t' or ',' or
	 * ' ')
	 */
	public static final char defaultSeparator = '\t';
	
	public static final char defaultQuote = '\"';
	
	public static final char defaultEscape = '\\';

	private DefaultSchema schema;

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see SchemaReader#getSchema()
	 */
	@Override
	public Schema getSchema() {
		return schema;
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Load CSV schema", ProgressIndicator.UNKNOWN); //$NON-NLS-1$

		String namespace = CSVFileIO.CSVFILE_NS;
		schema = new DefaultSchema(namespace, getSource().getLocation());
	
		String separator = getParameter(PARAM_SEPARATOR);
		char sep = (separator == null || separator.isEmpty())?(defaultSeparator):(separator.charAt(0));
		String quote = getParameter(PARAM_QUOTE);
		char qu = (quote == null || quote.isEmpty())?(defaultQuote):(quote.charAt(0));
		String escape = getParameter(PARAM_ESCAPE);
		char esc = (escape == null || escape.isEmpty())?(defaultEscape):(escape.charAt(0));
		
		Reader streamReader = new BufferedReader(new InputStreamReader(
				getSource().getInput()));
		CSVReader reader = new CSVReader(streamReader, sep, qu, esc);

		String[] firstLine;

		try {
			// create type definition
			String typename = getParameter(PARAM_TYPENAME);
			DefaultTypeDefinition type = new DefaultTypeDefinition(new QName(
					typename));

			// constraints on main type
			type.setConstraint(MappableFlag.ENABLED);
			type.setConstraint(HasValueFlag.DISABLED);
			type.setConstraint(AbstractFlag.DISABLED);

			// set metadata for main type
			type.setLocation(getSource().getLocation());

			DefaultTypeDefinition propertyType = new DefaultTypeDefinition(
					new QName("string"));

			// set constraints on propertyType
			propertyType.setConstraint(HasValueFlag.ENABLED);
			propertyType.setConstraint(Binding.get(String.class));

			// initializes the first line of the table (names of the columns)
			firstLine = reader.readNext();

			// set properties for the main type
			for (String part : firstLine) {
				DefaultPropertyDefinition property = new DefaultPropertyDefinition(
						new QName(part), type, propertyType); // TODO

				// set constraints on property
				property.setConstraint(NillableFlag.DISABLED); // nillable
				property.setConstraint(Cardinality.CC_EXACTLY_ONCE); // cardinality

				// set metadata for property
				property.setLocation(getSource().getLocation());
			}

			schema.addType(type);

		} catch (Exception ex) {
			throw new RuntimeException(ex);

		}

		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * @see AbstractIOProvider#getDefaultContentType()
	 */
	@Override
	protected ContentType getDefaultContentType() {
		return CSVFileIO.CSVFILE_CT;
	}

}