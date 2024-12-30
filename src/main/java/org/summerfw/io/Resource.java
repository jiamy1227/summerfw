package org.summerfw.io;

import lombok.Data;

/**
 * @author: jiamy
 * @create: 2024/12/30 13:59
 **/
@Data
public class Resource {

   private String name;

   private String path;

   public Resource(String name, String path) {
      this.name = name;
      this.path = path;
   }
}
