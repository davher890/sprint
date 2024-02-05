package com.backend.sprint.model.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JobResultLog {

	private List<String> errorMessages = new ArrayList<String>();

	private List<String> infoMessages = new ArrayList<String>();

}
