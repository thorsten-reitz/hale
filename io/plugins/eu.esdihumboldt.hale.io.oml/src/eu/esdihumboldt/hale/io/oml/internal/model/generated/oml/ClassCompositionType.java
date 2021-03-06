//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.09.14 at 10:27:20 AM CEST 
//


package eu.esdihumboldt.hale.io.oml.internal.model.generated.oml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClassCompositionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClassCompositionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="operator" type="{http://www.omwg.org/TR/d7/ontology/alignment}ClassOperatorType"/>
 *         &lt;choice>
 *           &lt;element name="collection" type="{http://www.omwg.org/TR/d7/ontology/alignment}ClassCollectionType"/>
 *           &lt;element ref="{http://www.omwg.org/TR/d7/ontology/alignment}FeatureClass"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClassCompositionType", propOrder = {
    "operator",
    "collection",
    "featureClass"
})
public class ClassCompositionType {

    @XmlElement(required = true)
    protected ClassOperatorType operator;
    protected ClassCollectionType collection;
    @XmlElement(name = "FeatureClass")
    protected ClassType featureClass;

    /**
     * Gets the value of the operator property.
     * 
     * @return
     *     possible object is
     *     {@link ClassOperatorType }
     *     
     */
    public ClassOperatorType getOperator() {
        return operator;
    }

    /**
     * Sets the value of the operator property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassOperatorType }
     *     
     */
    public void setOperator(ClassOperatorType value) {
        this.operator = value;
    }

    /**
     * Gets the value of the collection property.
     * 
     * @return
     *     possible object is
     *     {@link ClassCollectionType }
     *     
     */
    public ClassCollectionType getCollection() {
        return collection;
    }

    /**
     * Sets the value of the collection property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassCollectionType }
     *     
     */
    public void setCollection(ClassCollectionType value) {
        this.collection = value;
    }

    /**
     * Gets the value of the featureClass property.
     * 
     * @return
     *     possible object is
     *     {@link ClassType }
     *     
     */
    public ClassType getFeatureClass() {
        return featureClass;
    }

    /**
     * Sets the value of the featureClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassType }
     *     
     */
    public void setFeatureClass(ClassType value) {
        this.featureClass = value;
    }

}
