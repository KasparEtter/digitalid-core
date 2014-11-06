package ch.virtualid.contact;

import ch.virtualid.agent.AgentPermissions;
import ch.virtualid.annotations.Capturable;
import ch.virtualid.annotations.Pure;
import ch.virtualid.exceptions.external.ExternalException;
import ch.virtualid.exceptions.packet.PacketException;
import ch.virtualid.identifier.IdentifierClass;
import ch.virtualid.identity.SemanticType;
import ch.virtualid.interfaces.Blockable;
import ch.virtualid.util.FreezableArrayList;
import ch.virtualid.util.FreezableLinkedHashSet;
import ch.virtualid.util.FreezableList;
import ch.virtualid.util.ReadonlyList;
import ch.xdf.Block;
import ch.xdf.ListWrapper;
import java.io.IOException;
import java.sql.SQLException;
import javax.annotation.Nonnull;

/**
 * This class models a freezable set of attribute types.
 * 
 * @see Authentications
 * @see ContactPermissions
 * 
 * @author Kaspar Etter (kaspar.etter@virtualid.ch)
 * @version 2.0
 */
public class AttributeSet extends FreezableLinkedHashSet<SemanticType> implements ReadonlyAttributeSet, Blockable {
    
    /**
     * Stores the semantic type {@code list.attribute.type@virtualid.ch}.
     */
    public static final @Nonnull SemanticType TYPE = SemanticType.create("list.attribute.type@virtualid.ch").load(ListWrapper.TYPE, SemanticType.ATTRIBUTE_IDENTIFIER);
    
    
    /**
     * Creates an empty set of attribute types.
     */
    public AttributeSet() {}
    
    /**
     * Creates a new attribute set with the given attribute type.
     * 
     * @param type the attribute type to add to the new set.
     * 
     * @require type.isAttributeType() : "The type is an attribute type.";
     * 
     * @ensure areSingle() : "The new attribute set contains a single element.";
     */
    public AttributeSet(@Nonnull SemanticType type) {
        assert type.isAttributeType() : "The type is an attribute type.";
        
        add(type);
    }
    
    /**
     * Creates a new attribute set from the given attribute set.
     * 
     * @param attributeSet the attribute set to add to the new attribute set.
     */
    public AttributeSet(@Nonnull ReadonlyAttributeSet attributeSet) {
        addAll(attributeSet);
    }
    
    /**
     * Creates a new attribute set from the given block.
     * 
     * @param block the block containing the attribute set.
     * 
     * @require block.getType().isBasedOn(getType()) : "The block is based on the indicated type.";
     */
    public AttributeSet(@Nonnull Block block) throws SQLException, IOException, PacketException, ExternalException {
        assert block.getType().isBasedOn(getType()) : "The block is based on the indicated type.";
        
        final @Nonnull ReadonlyList<Block> elements = new ListWrapper(block).getElementsNotNull();
        for (final @Nonnull Block element : elements) {
            final @Nonnull SemanticType type = IdentifierClass.create(element).getIdentity().toSemanticType();
            type.checkIsAttributeType();
            add(type);
        }
    }
    
    /**
     * @ensure return.isBasedOn(TYPE) : "The returned type is based on the indicated type.";
     */
    @Pure
    @Override
    public @Nonnull SemanticType getType() {
        return TYPE;
    }
    
    @Pure
    @Override
    public final @Nonnull Block toBlock() {
        final @Nonnull FreezableList<Block> elements = new FreezableArrayList<Block>(size());
        for (final @Nonnull SemanticType type : this) {
            elements.add(type.getAddress().toBlock().setType(SemanticType.ATTRIBUTE_IDENTIFIER));
        }
        return new ListWrapper(getType(), elements.freeze()).toBlock();
    }
    
    
    @Override
    public @Nonnull ReadonlyAttributeSet freeze() {
        super.freeze();
        return this;
    }
    
    
    @Pure
    @Override
    public final boolean areSingle() {
        return size() == 1;
    }
    
    
    /**
     * @require type.isAttributeType() : "The type is an attribute type.";
     */
    @Override
    public final boolean add(@Nonnull SemanticType type) {
        assert type.isAttributeType() : "The type is an attribute type.";
        
        return super.add(type);
    }
    
    /**
     * Adds the given attribute set to this attribute set.
     * 
     * @param attributeSet the attribute set to add to this attribute set.
     * 
     * @require isNotFrozen() : "This object is not frozen.";
     */
    public final void addAll(@Nonnull ReadonlyAttributeSet attributeSet) {
        for (final @Nonnull SemanticType type : attributeSet) {
            add(type);
        }
    }
    
    
    @Pure
    @Override
    public @Capturable @Nonnull AttributeSet clone() {
        return new AttributeSet(this);
    }
    
    
    @Pure
    @Override
    public final @Capturable @Nonnull AgentPermissions toAgentPermissions() {
        final @Nonnull AgentPermissions permissions = new AgentPermissions();
        for (final @Nonnull SemanticType type : this) {
            permissions.put(type, false);
        }
        return permissions;
    }
    
    
    @Pure
    @Override
    public final @Nonnull String toString() {
        final @Nonnull StringBuilder string = new StringBuilder("{");
        for (final @Nonnull SemanticType type : this) {
            if (string.length() != 1) string.append(", ");
            string.append(type.getAddress().getString());
        }
        string.append("}");
        return string.toString();
    }
    
}
