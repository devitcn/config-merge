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

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * 将母版yaml中的key=value，替换成data yaml 中的key=value
 * <p>
 * 1 如果data yaml中有 file:// 将会尝试查找文件用文件内容替换
 * </p>
 * <p>
 * 用法:
 * <pre>merge.addPath(new File("src/test/resources").toPath());
 * Map<String, Object> data = merge.parseYaml(new File("case1-data.yaml"));
 * Map<String, Object> template = merge.parseYaml(new File("template.yaml"));
 * merge.fillTemplateWithData(template, data);
 * merge.writeYamlToFile(template, new File("config.yml"));</pre>
 */
public class YamlMerge {


  ////从YAML处借用的三个方法////
  public YamlMerge() {
    DumperOptions op = new DumperOptions();
    op.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    op.setAllowUnicode(true);
    this.yaml = new Yaml(op);
  }

  final Yaml yaml;

  public Map<String, Object> parseYaml(File file) throws IOException {
    return yaml.load(contentOfFile(searchFileInPath(file.toPath())));
  }

  public String dump(Map<String, Object> yaml) {
    return this.yaml.dumpAs(yaml, null,
        DumperOptions.FlowStyle.BLOCK);
  }

  public void writeYamlToFile(Map<String, Object> template, File file) throws IOException {
    yaml.dump(template, writeTo(file));
  }


  ///本模块的设计

  public void fillTemplateWithData(Map<String, Object> template, Object value)
      throws IOException {
    if (value instanceof Map) {
      for (Map.Entry<String, Object> item : ((Map<String, Object>) value)
          .entrySet()) {
        mergeSingleKeyAndValue(template, item.getKey(), item.getValue());
      }
    } else {
      System.out.println("类型不适配");
    }
  }

  protected void mergeSingleKeyAndValue(Map<String, Object> template, String key,
                                        Object value) throws IOException {
    if (value instanceof Map) {
      if (template.containsKey(key) && template.get(key) instanceof Map) {
        fillTemplateWithData((Map<String, Object>) template.get(key), value);
      } else {
        template.put(key, new HashMap<>());
        fillTemplateWithData((Map<String, Object>) template.get(key), value);
      }
    } else {
      if (value instanceof String) {
        template.put(key, expandUrl((String) value));
      } else {
        template.put(key, value);
      }
    }
  }


  protected String expandUrl(String url) throws IOException {
    if (url.startsWith("file://")) {
      for (Path base : findPath) {
        Path p = base.resolve(url.replace("file://", ""));
        if (Files.isRegularFile(p)) {
          return contentOfFile(p);
        }
      }
    }
    return url;
  }

  //////文件处理

  private Path searchFileInPath(Path toPath) throws FileNotFoundException {
    if (Files.isRegularFile(toPath)) {
      return toPath;
    }
    for (Path base : findPath) {
      Path p = base.resolve(toPath);
      if (Files.isRegularFile(p)) {
        return p;
      }
    }
    throw new FileNotFoundException(toPath.toString());
  }


  protected String contentOfFile(Path file) throws IOException {
    byte[] bytes = Files.readAllBytes(file);
    return new String(bytes, fileCharset);
  }

  protected Writer writeTo(File file) throws IOException {
    return Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);
  }

  /** YAML文件的charset */
  Charset fileCharset = StandardCharsets.UTF_8;

  public void setFileCharset(Charset fileCharset) {
    this.fileCharset = fileCharset;
  }

  Set<Path> findPath = new LinkedHashSet<>();

  /** 增加查找路径，yaml文件将从这些路径中搜索 */
  public void addPath(Path path) {
    findPath.add(path);
  }


}
