/*
 * Copyright 2015 Nicolas HERVE
 *
 * This file is part of Network Tycoon.
 *
 * Network Tycoon is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Network Tycoon is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Network Tycoon. If not, see <http://www.gnu.org/licenses/>.
 */
package name.herve.networktycoon;

/**
 * @author Nicolas HERVE
 */
public class ResourceType implements Comparable<ResourceType> {
	private String code;
	private String name;
	private boolean isJocker;

	public ResourceType(String code) {
		this(code, code);
	}

	public ResourceType(String code, String name) {
		this(code, name, false);
	}

	public ResourceType(String code, String name, boolean isJocker) {
		super();
		this.code = code;
		this.name = name;
		this.isJocker = isJocker;
	}

	@Override
	public int compareTo(ResourceType o) {
		return code.compareTo(o.code);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ResourceType other = (ResourceType) obj;
		if (code == null) {
			if (other.code != null) {
				return false;
			}
		} else if (!code.equals(other.code)) {
			return false;
		}
		return true;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	public boolean isJocker() {
		return isJocker;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setJocker(boolean isJocker) {
		this.isJocker = isJocker;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ResourceType [code=" + code + ", name=" + name + ", isJocker=" + isJocker + "]";
	}
}
