/*
 * Copyright © 2020 lxb (lxbzmy@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.devit.tools;

import java.io.File;
import java.util.Map;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

public class YamlMergeTest {

  YamlMerge merge = new YamlMerge();

  @Test
  public void single_merge() throws Exception {

    File out = new File("target/temp");
    out.mkdirs();

    merge.addPath(new File("src/test/resources").toPath());
    Map<String, Object> data = merge.parseYaml(new File("case1-data.yaml"));
    Map<String, Object> template = merge.parseYaml(new File("template.yaml"));
    merge.fillTemplateWithData(template, data);
    merge.writeYamlToFile(template, new File(out, "config.yml"));

    System.out.println("build yaml:");
    String yamlString = merge.dump(template);
    System.out.println(yamlString);


  }

  @Test
  public void root_key_is_file_name() throws Exception {
    File base = new File("src/test/resources");

    Yaml yaml = new Yaml();

    File out = new File("target/temp");
    out.mkdirs();

    merge.addPath(base.toPath());

    Map<String, Object> data = merge.parseYaml(new File("case2-data.yaml"));

    for (Map.Entry<String, Object> item : data.entrySet()) {

      Map<String, Object> template = merge.parseYaml(new File("template.yaml"));

      System.out.println("build yaml:" + item.getKey());
      merge.fillTemplateWithData(template, item.getValue());
      merge.writeYamlToFile(template, new File(out, item.getKey() + ".yml"));

      String yamlString = merge.dump(template);
      System.out.println(yamlString);
    }

  }


}
