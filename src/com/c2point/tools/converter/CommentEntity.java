package com.c2point.tools.converter;

public class CommentEntity extends AbstractEntity {

	private CommentEntity() {
		
		super( EntityType.COMMENT );
		
	}

	public CommentEntity( String comment ) {
		
		this();
		setValue( comment );
	}

	public String toString() {
		
		return "// " + getValue();
	}
	
}
