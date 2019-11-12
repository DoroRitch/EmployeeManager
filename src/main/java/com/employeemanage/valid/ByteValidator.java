package com.employeemanage.valid;

import java.nio.charset.Charset;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ByteValidator implements ConstraintValidator<Byte, String>{

	private int min;
	private int max;
	private String charset;

	@Override
	public void initialize(Byte annotation) {
		min = annotation.min();
		max = annotation.max();
		charset = annotation.charset();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext ctx) {

		if (value == null) {
			return true;
		}

		byte[] b = value.getBytes(Charset.forName(charset));

		return min <= b.length && b.length <= max;
	}

}
