// Screen size
int ssize = 300;
// Array to store the heat values for each pixel
float heatmap[][][] = new float[2][ssize][ssize];
// The index of the current heatmap
int index = 0;
// A color gradient to see pretty colors
Gradient g;

void setup()
{
  size(300, 300);

  g = new Gradient();
  g.addColor(color(0, 0, 0));
  g.addColor(color(102, 0, 102));
  g.addColor(color(0, 144, 255));
  g.addColor(color(0, 255, 207));
  g.addColor(color(51, 204, 102));
  g.addColor(color(111, 255, 0));
  g.addColor(color(191, 255, 0));
  g.addColor(color(255, 240, 0));
  g.addColor(color(255, 153, 102));
  g.addColor(color(204, 51, 0));
  g.addColor(color(153, 0, 0));

  // Initalize the heat map (make sure everything is 0.0)
  for(int i = 0; i < ssize; ++i)
    for(int j = 0; j < ssize; ++j)
      heatmap[index][i][j] = 0.0;
}

void draw()
{
  // See if heat (or cold) needs applied
  if (mousePressed && (mouseButton == LEFT))
    apply_heat(mouseX, mouseY, 15, .5);
  if (mousePressed && (mouseButton == RIGHT))
    apply_heat(mouseX, mouseY, 15, -.25);

  // Calculate the next step of the heatmap
  update_heatmap();

  // For each pixel, translate its heat to the appropriate color
  for(int i = 0; i < ssize; ++i)
    for(int j = 0; j < ssize; ++j)
      set(i, j, g.getGradient(heatmap[index][i][j]));
}

void update_heatmap()
{
  // Calculate the new heat value for each pixel
  for(int i = 0; i < ssize; ++i)
    for(int j = 0; j < ssize; ++j)
      heatmap[index ^ 1][i][j] = calc_pixel(i, j);

  // flip the index to the next heatmap
  index ^= 1;
}

float calc_pixel(int i, int j)
{
  float total = 0.0;
  int count = 0;

  // This is were the magic happens...
  // Average the heat around the current pixel to determin the new value
  for(int ii = -1; ii < 2; ++ii)
  {
    for(int jj = -1; jj < 2; ++jj)
    {
      if(i + ii < 0 || i + ii >= width || j + jj < 0 || j + jj >= height)
        continue;

      ++count;
      total += heatmap[index][i + ii][j + jj];
    }
  }

  // return the average
  return total / count;
}

void apply_heat(int i, int j, int r, float delta)
{
  // apply delta heat (or remove it) at location
  // (i, j) with radius r
  for(int ii = -(r / 2); ii < (r / 2); ++ii)
  {
    for(int jj = -(r / 2); jj < (r / 2); ++jj)
    {
      if(i + ii < 0 || i + ii >= width || j + jj < 0 || j + jj >= height)
        continue;

      // apply the heat
      heatmap[index][i + ii][j + jj] += delta;
      heatmap[index][i + ii][j + jj] = constrain(heatmap[index][i + ii][j + jj], 0.0, 20.0);
    }
  }
}