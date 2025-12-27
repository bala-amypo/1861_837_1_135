import os
import json

# The name of the script that will be generated
output_script = "setup_project_auto.py"

def get_project_files(root_dir):
    file_data = {}
    # Walk through the directory
    for root, dirs, files in os.walk(root_dir):
        # Exclude unnecessary directories
        if 'target' in dirs:
            dirs.remove('target')
        if '.git' in dirs:
            dirs.remove('.git')
        if '.mvn' in dirs:
            dirs.remove('.mvn')
        if '.idea' in dirs:
            dirs.remove('.idea')
        
        for file in files:
            # Exclude this script and the output script
            if file in [output_script, 'project_packer.py', 'setup_project.py']:
                continue
            # Exclude binary or unnecessary files
            if file.endswith(('.class', '.jar', '.log', '.DS_Store')):
                continue
            
            path = os.path.join(root, file)
            # Get relative path to use as key
            rel_path = os.path.relpath(path, root_dir)
            # Normalize path separators for cross-platform compatibility
            rel_path = rel_path.replace('\\', '/')
            
            try:
                with open(path, 'r', encoding='utf-8') as f:
                    content = f.read()
                file_data[rel_path] = content
            except Exception as e:
                print(f"Skipping binary or unreadable file {rel_path}: {e}")

    return file_data

def generate_installer(file_data):
    # Create the content of the new script
    script_content = f'''import os

# Dictionary containing file paths and their contents
FILES = {json.dumps(file_data, indent=4)}

def recreate_project():
    print("Starting project recreation...")
    for file_path, content in FILES.items():
        # Handle path separators for the current OS
        file_path = os.path.normpath(file_path)
        dir_name = os.path.dirname(file_path)
        
        if dir_name:
            os.makedirs(dir_name, exist_ok=True)
        
        try:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Created: {{file_path}}")
        except Exception as e:
            print(f"Error creating {{file_path}}: {{e}}")
            
    print("-" * 50)
    print("Project recreated successfully!")

if __name__ == "__main__":
    recreate_project()
'''
    
    # Write the generated script to disk
    with open(output_script, 'w', encoding='utf-8') as f:
        f.write(script_content)
    print(f"Successfully generated restoration script: {output_script}")

if __name__ == "__main__":
    current_dir = os.getcwd()
    print(f"Scanning directory: {current_dir}")
    data = get_project_files(current_dir)
    generate_installer(data)